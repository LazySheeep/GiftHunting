package io.lazysheeep.gifthunting.game;

public abstract class State<SM extends StateMachine<SM, S>, S extends Enum<S>>
{
    public abstract void onEnter(SM stateMachine);

    public abstract void onUpdate(SM stateMachine);

    public abstract void onExit(SM stateMachine);
}

