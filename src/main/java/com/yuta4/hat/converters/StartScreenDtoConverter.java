package com.yuta4.hat.converters;

import com.yuta4.hat.dto.StartScreenDto;
import com.yuta4.hat.entities.Game;
import com.yuta4.hat.entities.Player;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StartScreenDtoConverter implements Converter<Player, StartScreenDto> {

    @Override
    public StartScreenDto convert(Player player) {
        Long lastGameId = null;
        boolean isActive = false;
        Game game = player.getLastGame();
        if(game != null) {
            boolean isGamePlayer = game.getTeams().stream().flatMap(team -> team.getPlayers().stream())
                    .anyMatch(pl -> pl.equals(player));
            if(game.getOwner().equals(player) || isGamePlayer) {
                lastGameId = game.getId();
                isActive = !Boolean.FALSE.equals(game.getIsActive());
            }
        }
        return new StartScreenDto(lastGameId, isActive);
    }

}
