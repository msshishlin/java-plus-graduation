package ru.practicum.collector.configuration;

/**
 * Топик Kafka.
 */
public enum KafkaTopic {
    /**
     * Топик, содержащий данные о сходстве мероприятий.
     */
    EVENTS_SIMILARITY,

    /**
     * Топик, содержащий данные о последней оценке (максимальном весе действия) пользователей мероприятий.
     */
    USER_ACTIONS;

    /**
     * Проверить содержит ли перечисление полученное значение.
     *
     * @param value значение.
     * @return признак содержит ли перечисление полученное значение.
     */
    public static boolean contains(String value) {
        for (KafkaTopic kafkaTopic : values()) {
            if (kafkaTopic.name().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
