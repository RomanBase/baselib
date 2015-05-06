package com.base.lib.engine;

/**
 *
 */
public class InnerActions { //todo interpolate between actions

    protected InnerAction[] actions;
    protected InnerAction currentAction;
    private int currentActionIndex;

    /** 2nd action start pos and rot is set to 1st action start pos and rot., and so on..,*/
    public InnerActions(InnerAction... innerActions){

        actions = innerActions;

        currentAction = actions[0];
        for(int i = 1; i<actions.length; i++){
            actions[i].setsPos(currentAction.getfPos());
            actions[i].setsRot(currentAction.getfRot());
            actions[i].setsColor(currentAction.getfColor());
            currentAction = actions[i];
        }
        reset();
    }

    public InnerActions(){

    }

    public void reverse(){

        InnerAction[] temp = new InnerAction[actions.length];
        System.arraycopy(actions, 0, temp, 0, actions.length);
        for(int i = 0; i<actions.length; i++){
            temp[i].reverse();

            actions[actions.length-1-i] = temp[i];
        }

        currentActionIndex = 1;
    }

    public InnerAction next(){

        if(currentAction.isDone()){
           if(currentActionIndex < actions.length){
               currentAction = actions[currentActionIndex++];
           }
        }

        return currentAction.next();
    }

    public void reset(){

        currentAction = actions[0];
        currentActionIndex = 1;
        for(InnerAction action : actions){
            action.t = 0.0f;
        }
    }

    public boolean isDone(){

        return actions[actions.length-1].isDone();
    }

}
