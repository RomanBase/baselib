import bpy
import struct
import mathutils
from mathutils import Matrix, Euler
from math import degrees
from bpy.props import *
from bpy_extras.io_utils import ExportHelper

bl_info = {
    "name": "Export Base Line Format (.bel)",
    "description": "Export Base Engine file data (.bel)",
    "author": "base",
    "version": (0, 256),
    "blender": (2, 69, 0),
    "location": "File > Export > BaseLine (.bel)",
    "warning": "",
    "wiki_url": "http://wiki.blender.org/",
    "tracker_url": "https://projects.blender.org/tracker/",
    "category": "Import-Export"}

def veckey3d(v):
    return round(v[0], 6), round(v[2], 6), round(v[1], 6)
    
def do_export(context, props, filepath, selected, points, dim3):

    file = open(filepath, "w", encoding="utf8", newline="\n")
    fw = file.write
    
    version = '0.256'
    fw('\nBlender export\n')
    fw('version %s' % version)
    
    empties = []
    scene = context.scene
    objs = []
    objects = scene.objects if not selected else context.selected_objects
    for ob_main in objects:
        obs = [(ob_main, ob_main.matrix_world)]
        for ob, ob_mat in obs:
            try:
                me = ob.to_mesh(scene, False, 'PREVIEW')
            except RuntimeError:
                me = None
                
            if me is None:
                ### EXPORT EMPTY -- AS SENSOR ###
                if ob.type == "EMPTY":
                    name = ob.name
                    dim = [round(ob.scale.x, 6), round(ob.scale.z, 6)] if not dim3 else [round(ob.scale.x, 6), round(ob.scale.z, 6), round(ob.scale.y, 6)]
                    pos = [round(ob_mat[0][3], 6), round(ob_mat[2][3], 6)] if not dim3 else [round(ob_mat[0][3], 6), round(ob_mat[2][3], 6), round(ob_mat[1][3], 6)]
                    rot = -round(ob.rotation_euler.y, 6)
                    empties.append([name, pos, dim, rot])
                continue
                
            me.transform(mathutils.Matrix()*ob_mat)
            me_verts = me.vertices[:]
                
            verts = []
            for v in me_verts:
                vout = veckey3d(v.co)
                verts.append([vout[0], vout[1], vout[2]])
                
            vouts = [];
            if not points:
                print("exp edges")
                prevIndex = -1
                outIndex = -1
                for ed in me.edges:
                    if ed.is_loose:
                        findex = ed.vertices[0]
                        sindex = ed.vertices[1]
                        
                        if findex > sindex:
                            temp = findex
                            findex = sindex
                            sindex = temp
                        
                        if findex != prevIndex:
                            vouts.append([])
                            outIndex += 1
                            vouts[outIndex].append(verts[findex])
                            
                        vouts[outIndex].append(verts[sindex])
                        prevIndex = sindex
            else:
                print("exp points")
                vouts.append(verts[:])
            
            ### FILE WRITING ###
            dim = (2 if not dim3 else 3) 
            for array in vouts:
                fw('\n\nob %s %s\n' % (ob.name, dim))
                count = len(array) * dim
                print(ob.name)
                print(count)
                fw('l %s { ' % count)
                if dim3:
                    for v in array:
                        fw('%s ' % v[0])
                        fw('%s ' % v[1])
                        fw('%s ' % v[2])
                else:
                    for v in array:
                        fw('%s ' % v[0])
                        fw('%s ' % v[1])
                fw('}')
    
    ### FILE WRITING ###
    eLen = len(empties)
    if eLen > 0:
        fw('\n\ns %s {' % eLen)
        if not dim3:
            for name, pos, dim, rot in empties:
                fw('\n%s ' % name)
                fw('%s %s ' % (pos[0], pos[1]))
                fw('%s %s ' % (dim[0], dim[1]))
                fw('%s' % rot)
            fw('\n}')
        else:
            for name, pos, dim, rot in empties:
                fw('\n%s ' % name)
                fw('%s %s %s ' % (pos[0], pos[1], pos[2]))
                fw('%s %s %s ' % (dim[0], dim[1], dim[2]))
                fw('%s' % rot)
            fw('\n}')
            
    file.close()
    return True

##### EXPORT OPERATOR #######
class Export_beo(bpy.types.Operator, ExportHelper):
    bl_idname = "base_export.bel"
    bl_label = "Export Base Line (.bel)"
    
    filename_ext = ".bel"
    filter_glob = StringProperty(default="*.bel", options={'HIDDEN'})
    
    selected = BoolProperty(
            name="Only Selected",
            description="Export only selected object",
            default=False
            )
            
    points = BoolProperty(
            name="Only Points",
            description="Export object vertices as points",
            default=False
    )
    
    dim3 = BoolProperty(
            name="3D",
            description="Export 3 dimensional curve",
            default=False
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
    self.layout.operator(Export_beo.bl_idname, text="BaseLine (.bel)")

def register():
    bpy.utils.register_module(__name__)
    
    bpy.types.INFO_MT_file_export.append(menu_func)

def unregister():
    bpy.utils.unregister_module(__name__)
    
    bpy.types.INFO_MT_file_export.remove(menu_func)

if __name__ == "__main__":
    register()