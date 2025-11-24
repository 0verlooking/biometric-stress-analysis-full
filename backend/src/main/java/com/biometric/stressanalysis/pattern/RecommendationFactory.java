package com.biometric.stressanalysis.pattern;

import com.biometric.stressanalysis.entity.Recommendation;
import com.biometric.stressanalysis.entity.StressAnalysis;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory Pattern for creating recommendations based on stress analysis
 * Demonstrates Factory Method design pattern
 */
@Component
public class RecommendationFactory {

    /**
     * Creates recommendations based on stress analysis results
     * @param analysis the stress analysis to base recommendations on
     * @return list of recommendations
     */
    public List<Recommendation> createRecommendations(StressAnalysis analysis) {
        List<Recommendation> recommendations = new ArrayList<>();

        switch (analysis.getStressLevel()) {
            case LOW -> recommendations.addAll(createLowStressRecommendations());
            case MODERATE -> recommendations.addAll(createModerateStressRecommendations());
            case HIGH -> recommendations.addAll(createHighStressRecommendations());
            case CRITICAL -> recommendations.addAll(createCriticalStressRecommendations());
        }

        // Add specific recommendations based on component scores
        if (analysis.getCardiovascularScore() > 50) {
            recommendations.add(createCardiovascularRecommendation());
        }

        if (analysis.getSleepScore() > 50) {
            recommendations.add(createSleepRecommendation());
        }

        if (analysis.getBiochemicalScore() > 50) {
            recommendations.add(createBiochemicalRecommendation());
        }

        return recommendations;
    }

    private List<Recommendation> createLowStressRecommendations() {
        List<Recommendation> recommendations = new ArrayList<>();

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.PHYSICAL_ACTIVITY)
                .title("Підтримуйте активний спосіб життя")
                .description("Продовжуйте займатися фізичною активністю 3-4 рази на тиждень по 30 хвилин.")
                .priority(Recommendation.Priority.LOW)
                .build());

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.LIFESTYLE)
                .title("Збалансоване харчування")
                .description("Підтримуйте здорове харчування з достатньою кількістю овочів та фруктів.")
                .priority(Recommendation.Priority.LOW)
                .build());

        return recommendations;
    }

    private List<Recommendation> createModerateStressRecommendations() {
        List<Recommendation> recommendations = new ArrayList<>();

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.RELAXATION)
                .title("Практикуйте техніки релаксації")
                .description("Спробуйте медитацію, йогу або дихальні вправи 15-20 хвилин щодня.")
                .priority(Recommendation.Priority.MEDIUM)
                .build());

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.PHYSICAL_ACTIVITY)
                .title("Регулярні фізичні вправи")
                .description("Збільште фізичну активність до 4-5 разів на тиждень.")
                .priority(Recommendation.Priority.MEDIUM)
                .build());

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.SLEEP)
                .title("Покращення якості сну")
                .description("Дотримуйтесь режиму сну, лягайте і вставайте в один і той же час.")
                .priority(Recommendation.Priority.MEDIUM)
                .build());

        return recommendations;
    }

    private List<Recommendation> createHighStressRecommendations() {
        List<Recommendation> recommendations = new ArrayList<>();

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.MEDICAL)
                .title("Консультація лікаря")
                .description("Рекомендується консультація з лікарем для оцінки стану здоров'я.")
                .priority(Recommendation.Priority.HIGH)
                .build());

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.RELAXATION)
                .title("Інтенсивна релаксаційна терапія")
                .description("Розгляньте можливість відвідування психолога або психотерапевта.")
                .priority(Recommendation.Priority.HIGH)
                .build());

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.LIFESTYLE)
                .title("Зміна способу життя")
                .description("Перегляньте свій розклад дня, зменште робоче навантаження, приділіть більше часу відпочинку.")
                .priority(Recommendation.Priority.HIGH)
                .build());

        return recommendations;
    }

    private List<Recommendation> createCriticalStressRecommendations() {
        List<Recommendation> recommendations = new ArrayList<>();

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.MEDICAL)
                .title("ТЕРМІНОВА медична консультація")
                .description("КРИТИЧНИЙ рівень стресу! Терміново зверніться до лікаря для повного обстеження.")
                .priority(Recommendation.Priority.URGENT)
                .build());

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.LIFESTYLE)
                .title("Негайна зміна способу життя")
                .description("Терміново зменште стресові фактори, візьміть відпустку якщо можливо.")
                .priority(Recommendation.Priority.URGENT)
                .build());

        recommendations.add(Recommendation.builder()
                .category(Recommendation.Category.RELAXATION)
                .title("Професійна психологічна допомога")
                .description("Негайно зверніться до психолога або психотерапевта.")
                .priority(Recommendation.Priority.URGENT)
                .build());

        return recommendations;
    }

    private Recommendation createCardiovascularRecommendation() {
        return Recommendation.builder()
                .category(Recommendation.Category.MEDICAL)
                .title("Моніторинг серцево-судинної системи")
                .description("Виявлено відхилення в роботі серцево-судинної системи. " +
                           "Рекомендується консультація кардіолога.")
                .priority(Recommendation.Priority.HIGH)
                .build();
    }

    private Recommendation createSleepRecommendation() {
        return Recommendation.builder()
                .category(Recommendation.Category.SLEEP)
                .title("Покращення гігієни сну")
                .description("Проблеми зі сном. Дотримуйтесь режиму сну (7-9 годин), " +
                           "уникайте кофеїну та екранів перед сном.")
                .priority(Recommendation.Priority.MEDIUM)
                .build();
    }

    private Recommendation createBiochemicalRecommendation() {
        return Recommendation.builder()
                .category(Recommendation.Category.MEDICAL)
                .title("Аналіз гормонального профілю")
                .description("Підвищений рівень кортизолу. Рекомендується консультація ендокринолога " +
                           "для перевірки гормонального балансу.")
                .priority(Recommendation.Priority.HIGH)
                .build();
    }
}
