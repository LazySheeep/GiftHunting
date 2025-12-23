package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.GiftHunting;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class StateMachine<SM extends StateMachine<SM, S>, S extends Enum<S>>
{
    protected S _currentStateEnum;
    protected State<SM, S> _currentState;
    private final Map<S, State<SM, S>> _stateCache = new HashMap<>();

    protected StateMachine(S initialState)
    {
        this._currentStateEnum = initialState;
        this._currentState = getStateCached(initialState);
        _currentState.onEnter((SM)this);
    }

    public S getCurrentStateEnum()
    {
        return _currentStateEnum;
    }

    public void switchState(S newStateEnum)
    {
        if(newStateEnum == _currentStateEnum)
            return;

        S oldStateEnum = _currentStateEnum;
        State<SM, S> oldState = _currentState;
        State<SM, S> newState = getStateCached(newStateEnum);
        oldState.onExit((SM)this);
        newState.onEnter((SM)this);
        _currentStateEnum = newStateEnum;
        _currentState = newState;
        GiftHunting.Log(Level.INFO, "Game state changed: " + oldStateEnum + " -> " + newStateEnum);
    }

    public void tick()
    {
        if(_currentState != null) _currentState.onUpdate((SM)this);
    }

    private State<SM, S> getStateCached(S stateEnum)
    {
        if(_stateCache.containsKey(stateEnum))
            return _stateCache.get(stateEnum);
        State<SM, S> state = createState(stateEnum);
        _stateCache.put(stateEnum, state);
        return state;
    }

    protected abstract State<SM, S> createState(S state);
}
