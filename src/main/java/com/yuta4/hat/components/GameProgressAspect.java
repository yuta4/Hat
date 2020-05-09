package com.yuta4.hat.components;

import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Team;
import com.yuta4.hat.events.GameProgressEvent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GameProgressAspect {

    private ApplicationListener<GameProgressEvent> gameProgressListener;

    public GameProgressAspect(ApplicationListener<GameProgressEvent> gameProgressListener) {
        this.gameProgressListener = gameProgressListener;
    }

    @After("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public void raiseEvent(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if(!methodName.equals("save") && !methodName.equals("delete")) {
            return;
        }
        Object arg = joinPoint.getArgs()[0];
        if(arg instanceof Game) {
            gameProgressListener.onApplicationEvent(new GameProgressEvent((Game) arg));
        } else if (arg instanceof Team) {
            gameProgressListener.onApplicationEvent(new GameProgressEvent(((Team) arg).getGame()));
        }
    }
}
