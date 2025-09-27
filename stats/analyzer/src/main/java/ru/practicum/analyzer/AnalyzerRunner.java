package ru.practicum.analyzer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.kafka.processor.EventSimilarityProcessor;
import ru.practicum.analyzer.kafka.processor.UserActionProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    private final EventSimilarityProcessor eventSimilarityProcessor;
    private final UserActionProcessor userActionProcessor;

    @Override
    public void run(String... args) {
        eventSimilarityProcessor.start();
        userActionProcessor.start();
    }
}