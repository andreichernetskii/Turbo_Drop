package dispatcher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
//@RequestMapping("/simulate")
@RestController
public class SimulationController {

    private final UpdateProcessor updateProcessor;

    /**
     * Simulates an incoming update to test the bot.
     *
     * @param update the incoming update to process.
     */
    @PostMapping("/update")
    public void simulateUpdate(@RequestBody Update update) {
        updateProcessor.processUpdate(update);
    }
}
