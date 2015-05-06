import bpy
import struct
import mathutils
from mathutils import Matrix, Euler
from math import degrees
from bpy.props import *
from bpy_extras.io_utils import ExportHelper

bl_info = {
    "name": "Export Base Format (.beo)",
    "description": "Export Base Engine file data (.beo)",
    "author": "base",
    "version": (0, 256),
    "blender": (2, 69, 0),
    "location": "File > Export > Base (.beo)",
    "warning": "",
    "wiki_url": "http://wiki.blender.org/",
    "tracker_url": "https://projects.blender.org/tracker/",
    "category": "Import-Export"}

def mesh_triangulate(me):
    import bmesh
    bm = bmesh.new()
    bm.from_mesh(me)
    bmesh.ops.triangulate(bm, faces=bm.faces)
    bm.to_mesh(me)
    bm.free()
    
def veckey3d(v):
    return round(v.x, 6), round(v.y, 6), round(v.z, 6)

def veckey2d(v):
    return round(v[0], 6), round(v[1], 6)
    
def roundRot(rot):
    out = (-degrees(round(rot[0], 6)), -degrees(round(rot[1], 6)), -degrees(round(rot[2], 6)))
    if abs(out[0]) < 0.01:
        out = (0.0, out[1], out[2])
    if abs(out[1]) < 0.01:
        out = (out[0], 0.0, out[2])
    if abs(out[2]) < 0.01:
        out = (out[0], out[1], 0.0)
    return out;
    
def round3d(v):
    out = (round(v[0], 6), round(v[1], 6), round(v[2], 6))
    if abs(out[0]) < 1:
        out = (0, out[1], out[2])
    if abs(out[1]) < 1:
        out = (out[0], 0, out[2])
    if abs(out[2]) < 1:
        out = (out[0], out[1], 0)
    return out
    
def switchYZ(v):
    return v[0], v[2], v[1]
    
def duplicate(fv, ft, vertCoord, vertGroup, normCoord):
    count = len(fv)
    for i in range(0, count):
        index = fv[i]
        tex = ft[i]
        newIndex = len(vertCoord)
        add = True
        for k in range(i, count):
            if(index == fv[k]):
                if(tex != ft[k]):
                    if(add):
                        vert = vertCoord[index]
                        norm = normCoord[index]
                        if(not(vert[1] is None)): # if vert belongs to VertGroup
                            vertGroup[vert[1]].count += 1
                        vertCoord.append(vert)
                        normCoord.append(norm)
                        add = False
                    fv[k] = newIndex
                    
def texOrder(fv, ft, texCoord, vertCoord):
    temp = [None]*(len(vertCoord))
    for i in range(0, len(ft)):
        findex = fv[i]
        tindex = ft[i]
        if(temp[findex] is None):
            temp[findex] = texCoord[tindex]
    return temp
    
def bbox(vertCoord):
    minx = maxx = vertCoord[0][0][0]
    miny = maxy = vertCoord[0][0][1]
    minz = maxz = vertCoord[0][0][2]
    
    for v in vertCoord:
        vert = v[0]
        if(vert[0] < minx): minx = vert[0]
        if(vert[0] > maxx): maxx = vert[0]
        if(vert[1] < miny): miny = vert[1]
        if(vert[1] > maxy): maxy = vert[1]
        if(vert[2] < minz): minz = vert[2]
        if(vert[2] > maxz): maxz = vert[2]
    return [round(abs(minx-maxx), 6), round(abs(miny-maxy), 6), round(abs(minz-maxz), 6)]
    
def getGroupIndex(vertGroup, groupName):
    for group in vertGroup:
        if(group.name == groupName):
            return group

def flipDrawOrder(fv):
    even = 0
    for i in range(0, len(fv), 3):
        even += 1
        if(even % 2 == 0):
            temp = fv[i]
            fv[i] = fv[i+2]
            fv[i+2] = temp

class VertGroup():
    __slots__ = ("index", "name", "weight", "count", "stOffset", "vertices")
    def __init__(self, index, name, weight):
        self.index = index
        self.name = name
        self.weight = weight
        self.count = 0
        self.stOffset = 0
        self.vertices = []
        
    def len():
        return len(self.vertices)
        
    def __repr__(self):
        return "VertGroup()"
    
    def __str__(self):
        return str(self.index) +" "+ self.name +" "+ str(self.weight) +" "+ str(self.count) +" "+ str(self.stOffset)
    
def calcStartOffset(vertGroup):
    offset = 0
    for group in vertGroup:
        group.stOffset = offset
        offset += group.count

def sortVertGroups(vertGroup, vertCoord, texCoord, fv):
    tempTexCoord = [None]*(len(vertCoord))
    for i in range(0, len(fv)):
        f = fv[i]
        vert = vertCoord[f]
        v = vert[0]
        g = vert[1]
        nf = vert[2]
        if(nf is -1):
            gr = vertGroup[g]
            nf = gr.stOffset + len(gr.vertices)
            gr.vertices.append(v)
            vertCoord[f] = (v, g, nf)
            tempTexCoord[nf] = texCoord[f]
        fv[i] = nf
    return tempTexCoord
    
def exportArmature(context, fw):

    def ensure_rot_order(rot_order_str):
        if set(rot_order_str) != {'X', 'Y', 'Z'}:
            rot_order_str = "XYZ"
        return rot_order_str

    rotate_mode = 'NATIVE'
    root_transform_only = False

    obj = context.object
    arm = obj.data
    children = {None: []}
    
    #init blank bone list
    for bone in arm.bones:
        children[bone.name] = []
        
    for bone in arm.bones:
        children[getattr(bone.parent, "name", None)].append(bone.name)
        
    bone_struct = []    
    serialized_names = []
    node_locations = {}
    
    def getParentIndex(parent):
        index = 0;
        for name in serialized_names:
            if(name == parent):
                return index
            index += 1
        return -1
    
    def recursive_node(bone_name):
        my_children = children[bone_name]
        
        bone = arm.bones[bone_name]
        pose_bone = obj.pose.bones[bone_name]
        loc = bone.head_local
        tloc = bone.tail_local
        node_locations[bone_name] = loc
        print(loc)
        parentIndex = -1
        if bone.parent:
            parentIndex = getParentIndex(bone.parent.name)
        
        bone_struct.append([bone_name, parentIndex, switchYZ(veckey3d(loc)), switchYZ(veckey3d(tloc)), '1.0'])
        
        if my_children:
            for child_bone in my_children:
                serialized_names.append(child_bone)
                recursive_node(child_bone)
        
    if len(children[None]) == 1:
        key = children[None][0]
        serialized_names.append(key)
        recursive_node(key)
    else:
        for child_bone in children[None]:
            serialized_names.append(child_bone)
            recursive_node(child_bone)
        
    fw('skelet %i {\n' % len(bone_struct))    
    
    for bone in bone_struct:
        fw('%s %i ' % (bone[0], bone[1]))
        fw('%s ' % (bone[4]))
        fw('{ %s %s %s ' % (bone[2][:]))
        fw('%s %s %s }' % (bone[3][:]))
        fw('\n')
    fw('}\n')    
     
    class DecoratedBone(object):
        __slots__ = (
            "name",  # bone name, used as key in many places
            "parent",  # decorated bone parent, set in a later loop
            "rest_bone",  # blender armature bone
            "pose_bone",  # blender pose bone
            "pose_mat",  # blender pose matrix
            "rest_arm_mat",  # blender rest matrix (armature space)
            "rest_local_mat",  # blender rest batrix (local space)
            "pose_imat",  # pose_mat inverted
            "rest_arm_imat",  # rest_arm_mat inverted
            "rest_local_imat",  # rest_local_mat inverted
            "prev_euler",  # last used euler to preserve euler compability in between keyframes
            "skip_position",  # is the bone disconnected to the parent bone?
            "rot_order",
            "rot_order_str",
            "rot_order_str_reverse",  # needed for the euler order when converting from a matrix
            )

        _eul_order_lookup = {
            'XYZ': (0, 1, 2),
            'XZY': (0, 2, 1),
            'YXZ': (1, 0, 2),
            'YZX': (1, 2, 0),
            'ZXY': (2, 0, 1),
            'ZYX': (2, 1, 0),
            }

        def __init__(self, bone_name):
            self.name = bone_name
            self.rest_bone = arm.bones[bone_name]
            self.pose_bone = obj.pose.bones[bone_name]

            if rotate_mode == "NATIVE":
                self.rot_order_str = ensure_rot_order(self.pose_bone.rotation_mode)
            else:
                self.rot_order_str = rotate_mode
            self.rot_order_str_reverse = self.rot_order_str[::-1]

            self.rot_order = DecoratedBone._eul_order_lookup[self.rot_order_str]

            self.pose_mat = self.pose_bone.matrix

            # mat = self.rest_bone.matrix  # UNUSED
            self.rest_arm_mat = self.rest_bone.matrix_local
            self.rest_local_mat = self.rest_bone.matrix

            # inverted mats
            self.pose_imat = self.pose_mat.inverted()
            self.rest_arm_imat = self.rest_arm_mat.inverted()
            self.rest_local_imat = self.rest_local_mat.inverted()

            self.parent = None
            self.prev_euler = Euler((0.0, 0.0, 0.0), self.rot_order_str_reverse)
            self.skip_position = ((self.rest_bone.use_connect or root_transform_only) and self.rest_bone.parent)

        def update_posedata(self):
            self.pose_mat = self.pose_bone.matrix
            self.pose_imat = self.pose_mat.inverted()

        def __repr__(self):
            if self.parent:
                return "[\"%s\" child on \"%s\"]\n" % (self.name, self.parent.name)
            else:
                return "[\"%s\" root bone]\n" % (self.name)

    bones_decorated = [DecoratedBone(bone_name) for bone_name in serialized_names]

    # Assign parents
    bones_decorated_dict = {}
    for dbone in bones_decorated:
        bones_decorated_dict[dbone.name] = dbone

    for dbone in bones_decorated:
        parent = dbone.rest_bone.parent
        if parent:
            dbone.parent = bones_decorated_dict[parent.name]
    del bones_decorated_dict
    
    scene = context.scene
    
    currentFrame = scene.frame_current
    fr_start = scene.frame_start
    fr_end = scene.frame_end
    if fr_end == 0:
        fr_end = -1
    else:
        framesCount = fr_end - fr_start+1    
        fw('frames %i {\n' % framesCount)
        
    for frame in range(fr_start, fr_end+1):
        scene.frame_set(frame)

        for dbone in bones_decorated:
            dbone.update_posedata()

        for dbone in bones_decorated:
            trans = Matrix.Translation(dbone.rest_bone.head_local)
            itrans = Matrix.Translation(-dbone.rest_bone.head_local)

            if  dbone.parent:
                mat_final = dbone.parent.rest_arm_mat * dbone.parent.pose_imat * dbone.pose_mat * dbone.rest_arm_imat
                mat_final = itrans * mat_final * trans
                loc = mat_final.to_translation() + (dbone.rest_bone.head_local - dbone.parent.rest_bone.head_local)
            else:
                mat_final = dbone.pose_mat * dbone.rest_arm_imat
                mat_final = itrans * mat_final * trans
                loc = mat_final.to_translation() + dbone.rest_bone.head

            # keep eulers compatible, no jumping on interpolation.
            rot = mat_final.to_euler(dbone.rot_order_str_reverse, dbone.prev_euler)
            outRot = roundRot(rot)

            fw("%s %s %s " % (outRot[0], outRot[2], outRot[1]))

            dbone.prev_euler = rot
        fw("\n")
        
    if fr_end > 0:
        fw('}')
        
    scene.frame_set(currentFrame)
    
def do_export(context, props, filepath, animation, action, selected, object2D, textureCoords, normals, raw):
    """
    file = open(filepath, 'wb')
    
    data = bpy.context.active_object.data
    file.write(struct.pack('<f', data.vertices[0].co.x))
    
    file.flush()
    file.close()
    """
    file = open(filepath, "w", encoding="utf8", newline="\n")
    fw = file.write
    
    version = '0.256'
    fw('\nBlender export\n')
    fw('version %s\n' % version)
    
    object3D = not object2D
    exportTextureCoords = textureCoords
    exportNormals = normals
    exportInRawFormat = raw
    
    if(object3D):
        cpv = 3
    else:
        cpv = 2
    
    scene = context.scene
    objects = scene.objects if not selected else context.selected_objects
    if(not action):
        for ob_main in objects: 
            obs = [(ob_main, ob_main.matrix_world)]
            for ob, ob_mat in obs:
                try:
                    apply_modifiers = not animation #in toggle menu ? // False
                    me = ob.to_mesh(scene, False, 'PREVIEW')
                except RuntimeError:
                    me = None
                if me is None:
                    continue
                
                me.transform(mathutils.Matrix()*ob_mat)
                mesh_triangulate(me)
                me_verts = me.vertices[:]
                
                vertGroupNames = ob.vertex_groups.keys()
                currentVGroup = ''
                vgroupsMap = [[] for _i in range(len(me_verts))]
                for v_idx, v_ls in enumerate(vgroupsMap):
                    v_ls[:] = [(vertGroupNames[g.group], g.weight) for g in me_verts[v_idx].groups]
                    
                face_index_pairs = [(face, index) for index, face in enumerate(me.polygons)]
                uv_face_mapping = [None] * len(face_index_pairs)
                
                normCoord = []
                vertCoord = []
                vertGroup = []
                texCoord = []
                
                if(exportInRawFormat):
                
                    groupIndex = 0;
                    uv_layer = me.uv_layers.active.data
                    for face, index in face_index_pairs:
                        for vert in face.vertices:
                            v = me_verts[vert]
                            uv = uv_layer[vert].uv
                            uv[1] = 1.0 - uv[1]
                            vn = veckey3d(face.normal)
                            vr = veckey3d(v.co)
                            vout = [vr[0], vr[2], vr[1]]
                            group = vgroupsMap[v.index]
                            gname = None
                            for vGroupName, weight in group:
                                nextGroup = True
                                gname = vGroupName
                                for group in vertGroup:
                                    if group.name == vGroupName:
                                        nextGroup = False
                                        continue
                                if nextGroup:
                                    vertGroup.append(VertGroup(groupIndex, vGroupName, weight))
                                    groupIndex += 1
                            gr = None
                            if(gname != None):
                                gr = getGroupIndex(vertGroup, gname).index
                                vertGroup[gr].count += 1
                            vertCoord.append([vout, gr, -1])
                            normCoord.append([vn[0], vn[2], vn[1]])
                            texCoord.append(veckey2d(uv))
                    
                        
                else:
                    uv_unique_count = 0
                    uv = f_index = uv_index = uv_key = uv_val = uv_ls = None
                    
                    uv_texture = me.uv_textures.active.data[:]
                    uv_layer = me.uv_layers.active.data[:]
                    
                    groupIndex = 0;
                    for v in me_verts:
                        vn = veckey3d(v.normal)
                        vr = veckey3d(v.co)
                        vout = [vr[0], vr[2], vr[1]]
                        group = vgroupsMap[v.index]
                        gname = None
                        for vGroupName, weight in group:
                            nextGroup = True
                            gname = vGroupName
                            for group in vertGroup:
                                if group.name == vGroupName:
                                    nextGroup = False
                                    continue
                            if nextGroup:
                                vertGroup.append(VertGroup(groupIndex, vGroupName, weight))
                                groupIndex += 1
                        gr = None
                        if(gname != None):
                            gr = getGroupIndex(vertGroup, gname).index
                            vertGroup[gr].count += 1
                        vertCoord.append([vout, gr, -1])
                        normCoord.append([vn[0], vn[2], vn[1]])

                    uv_dict = {}
                    uv_get = uv_dict.get
                    for f, f_index in face_index_pairs:
                        uv_ls = uv_face_mapping[f_index] = []
                        for uv_index, l_index in enumerate(f.loop_indices):
                            uv = uv_layer[l_index].uv
                            if(uv.x == None): 
                                uv = [0, 0] 
                            uv_key = veckey2d(uv)
                            uv_val = uv_get(uv_key)
                            if uv_val is None:
                                uv_val = uv_dict[uv_key] = uv_unique_count
                                texCoord.append(veckey2d([uv.x, 1.0-uv.y]))
                                uv_unique_count += 1
                            uv_ls.append(uv_val)      
                    
                    fv = []
                    ft = []
                    for f, f_index in face_index_pairs:
                        f_v = [(vi, me_verts[v_idx], l_idx) for vi, (v_idx, l_idx) in enumerate(zip(f.vertices, f.loop_indices))]
                        for vi, v, li in f_v:
                            fv.append(v.index)
                            ft.append(uv_face_mapping[f_index][vi])
                     
                    duplicate(fv, ft, vertCoord, vertGroup, normCoord)
                    texCoord = texOrder(fv, ft, texCoord, vertCoord)
                    
                    if(animation):
                        calcStartOffset(vertGroup)
                        texCoord = sortVertGroups(vertGroup, vertCoord, texCoord, fv)
                    
                bb = bbox(vertCoord)    
                
                ### FILE WRITING ###
                fw('\nob %s\n' % ob.name)
                fw('cpv %i\n' % cpv)
                fw('bbox ')
                fw('%s ' % bb[0])
                fw('%s ' % bb[1])
                fw('%s\n' % bb[2])    
                if(animation):
                    fw('a %s groups\n' % len(vertGroup))
                    
                vcount = len(vertCoord)*cpv
                fw('v %i { ' % vcount)
                if(animation):
                    for g in vertGroup:
                        count = len(g.vertices)
                        if g.count > count:
                            for i in range(0, g.count-count):
                                g.vertices.append((0.0, 0.0, 0.0))
                                g.stOffset += 1
                    fw('\n')
                    for group in vertGroup:
                        fw('%s ' % group.name)
                        fw('%s ' % (len(group.vertices)*cpv))
                        for v in group.vertices:
                            if(object3D):
                                fw('%s ' % v[0])
                                fw('%s ' % v[1])
                                fw('%s ' % v[2])
                            else:
                                fw('%s ' % v[1])
                                fw('%s ' % v[2])
                        fw('\n')
                else:
                    for vert in vertCoord:
                        v = vert[0]
                        if(object3D):
                            fw('%s ' % v[0])
                            fw('%s ' % v[1])
                            fw('%s ' % v[2])
                        else:
                            fw('%s ' % v[1])
                            fw('%s ' % v[2])
                fw('}\n')
                
                if(exportNormals):
                    ncount = len(normCoord)*3
                    fw('n %i { ' % ncount)
                    for n in normCoord:
                        fw('%s ' % n[0])
                        fw('%s ' % n[1])
                        fw('%s ' % n[2])
                    fw('}\n')
                
                if(exportTextureCoords):
                    tcount = len(texCoord)*2
                    fw('t %i { ' % tcount)
                    for t in texCoord:
                        if(t != None):
                            fw('%s ' % t[0])
                            fw('%s ' % t[1])
                        else:
                            fw('0.0 0.0 ')
                    fw('}\n')
                
                if(not exportInRawFormat):
                    fcount = len(fv)
                    fw('f %i { ' % fcount)
                    for f in fv:
                        fw('%i ' % f)
                    fw('}\n')
            
    if(animation or action):  
        exportArmature(context, fw)
    
    file.close()
    return True

##### EXPORT OPERATOR #######
class Export_beo(bpy.types.Operator, ExportHelper):
    bl_idname = "base_export.beo"
    bl_label = "Export Base (.beo)"
    
    filename_ext = ".beo"
    filter_glob = StringProperty(default="*.beo", options={'HIDDEN'})
    
    animation = BoolProperty(
            name="Animation",
            description="Export skeletal data too. Note: armature must be selected",
            default=False,
            )
    
    action = BoolProperty(
            name="Action",
            description="Export only skeletal data. Note: armature must be selected",
            default=False,
            )
            
    selected = BoolProperty(
            name="Selected",
            description="Export only selected objects.",
            default=False,
            )
            
    object2D = BoolProperty(
            name="2D Object",
            description="Export two dimensional object",
            default=False,
            )
    
    textureCoords = BoolProperty(
            name="Texture UVs",
            description="Export texture coordinates",
            default=True,
            )
    
    normals = BoolProperty(
            name="Normals",
            description="Export normal vectors",
            default=False,
            )
    
    raw = BoolProperty(
            name="Raw data",
            description="Export each triangle separately",
            default=False,
            )
    
    def execute(self, context):
        props = self.properties
        filepath = self.filepath
        filepath = bpy.path.ensure_ext(filepath, self.filename_ext)
        
        keywords = self.as_keywords(ignore=("check_existing", "filter_glob"))
        
        do_export(context, props, **keywords)
        return {'FINISHED'}
        
    def invoke(self, context, event):
        wm = context.window_manager
        wm.fileselect_add(self)
        return {'RUNNING_MODAL'}

### REGISTER ###

def menu_func(self, context):
    self.layout.operator(Export_beo.bl_idname, text="Base (.beo)")

def register():
    bpy.utils.register_module(__name__)
    
    bpy.types.INFO_MT_file_export.append(menu_func)

def unregister():
    bpy.utils.unregister_module(__name__)
    
    bpy.types.INFO_MT_file_export.remove(menu_func)

if __name__ == "__main__":
    register()