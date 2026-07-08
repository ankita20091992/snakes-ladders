package com.game.controller;

import com.game.model.GameState;
import com.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

//class declaration block
//@RestController tell spring this class handles HTTP requests and returns JSON auto. 
@RestController
//@RequestMapping-starts all endpoints in this class with /api/game
@RequestMapping("/api/game")
//@CrossOrigin allow frontend to talk to this controller
@CrossOrigin(origins = "*")
public class GameController {
    //Inject the service
    //@Autowired-Spring automatically gives this controller the GameService it already created
    @Autowired
    private GameService gameService;
//@GetMapping-listens for GET /api/game/init
//calls initGame() on the service, 
// Spring automatically converts returned GameState object to JSON    
    @GetMapping("/init")
    public GameState initGame() {
        return gameService.initGame();
    }
// Roll endpoint
//listens for POST /api/game/roll
//every time player clicks roll, frontend calls this
    @PostMapping("/roll")
    public GameState rollDice() {
        return gameService.rollDice();
    }
//state endpoint
//just read and return the current state without changing anything
    @GetMapping("/state")
    public GameState getGameState() {
        GameState state = gameService.getGameState();
        if (state == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not started - call /api/game/init first");
        }
        return state;
    }
//Reset endpoint
@PostMapping("/reset")
    public GameState resetGame() {
        return gameService.resetGame();
    }
    
}