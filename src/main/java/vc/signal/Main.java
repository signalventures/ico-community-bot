package vc.signal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

/**
 * Register and start community bot.
 */
@ComponentScan("vc.signal")
public class Main implements CommandLineRunner {

  @Autowired private CommunityBot bot;

  @Override
  public void run(String... args) throws Exception {
    TelegramBotsApi botsApi = new TelegramBotsApi();
    botsApi.registerBot(bot);
  }

  public static void main(String[] args) throws TelegramApiRequestException {
    ApiContextInitializer.init();
    SpringApplication.run(Main.class, args);
  }
}