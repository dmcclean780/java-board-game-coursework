package com.example.viewmodel;

import com.example.model.GameModel;
import com.example.service.NavigationService;

public class TitleViewModel {

    private GameModel gameModel;
    private NavigationService navigationService;

    public TitleViewModel(GameModel gameModel, NavigationService navigationService) {
        this.gameModel = gameModel;
        this.navigationService = navigationService;
    }

    public void startNewGame() {
        SetupViewModel setupVM = new SetupViewModel(gameModel, navigationService);
        navigationService.navigateTo("setupScreen", setupVM);
    }

    public void openSettings() {
        SettingsViewModel settingsVM = new SettingsViewModel(gameModel, navigationService);
        navigationService.navigateTo("settings", settingsVM);
    }

    public void exitGame() {
        System.exit(0);
    }

}
