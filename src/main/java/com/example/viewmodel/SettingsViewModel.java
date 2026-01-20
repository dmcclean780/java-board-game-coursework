package com.example.viewmodel;

import com.example.service.NavigationService;
import com.example.model.GameModel;

public class SettingsViewModel {
    
    private NavigationService navigationService;
    private GameModel gameModel;

    public SettingsViewModel(GameModel gameModel, NavigationService navigationService) {
        this.gameModel = gameModel;
        this.navigationService = navigationService;
    }

    public void playGame() {
        SetupViewModel setupVM = new SetupViewModel(gameModel, navigationService);
        navigationService.navigateTo("setupScreen", setupVM);
    }
}
