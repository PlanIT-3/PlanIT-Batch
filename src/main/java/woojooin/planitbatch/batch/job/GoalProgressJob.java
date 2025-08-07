package woojooin.planitbatch.batch.job;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.batch.listener.JobExecutionTimeListener;
import woojooin.planitbatch.batch.processor.GoalProgressProcessor;
import woojooin.planitbatch.batch.reader.GoalReader;
import woojooin.planitbatch.batch.writer.GoalProgressWriter;
import woojooin.planitbatch.domain.vo.DailyGoalProgress;
import woojooin.planitbatch.domain.vo.Goal;

@Slf4j
@Configuration
public class GoalProgressJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private GoalReader goalReader;

    @Autowired
    private GoalProgressProcessor goalProgressProcessor;

    @Autowired
    private GoalProgressWriter goalProgressWriter;

    @Autowired
    private JobExecutionTimeListener jobExecutionTimeListener;

    @Bean("dailyGoalProgressJob")  // Bean 이름 변경
    public Job createGoalProgressJob() {
        return jobBuilderFactory.get("goalProgressJob")  // 내부 Job 이름은 유지
            .incrementer(new RunIdIncrementer()) // 자동으로 고유 파라미터 생성
            .listener(jobExecutionTimeListener)
            .start(createGoalProgressStep())
            .build();
    }

    @Bean("dailyGoalProgressStep")  // Bean 이름 변경
    public Step createGoalProgressStep() {
        return stepBuilderFactory.get("goalProgressStep")  // 내부 Step 이름은 유지
            .<List<Goal>, List<DailyGoalProgress>>chunk(1)
            .reader(goalReader)
            .processor(goalProgressProcessor)
            .writer(goalProgressWriter)
            .build();
    }
}