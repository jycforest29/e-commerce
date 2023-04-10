package com.jycforest29.commerce.common.config.batch;

import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Bean
    public JpaPagingItemReader<MadeOrder> itemReader(){
        LocalDateTime now = LocalDateTime.now();
        Map<String, LocalDateTime> map = new HashMap<>();
        map.put("now", now);
        return new JpaPagingItemReaderBuilder<MadeOrder>()
                .pageSize(10)
                .queryString("select m from MadeOrder m where timestampdiff(day, m.createdAt, :now) = 5")
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public ItemProcessor<MadeOrder, MadeOrder> itemProcessor(){
        return new ItemProcessor<MadeOrder, MadeOrder>() {
            @Override
            public MadeOrder process(MadeOrder madeOrder) throws Exception {
                if (madeOrder.getCancelAvaiable()){
                    madeOrder.setCancelAvaiable(false);
                }
                return madeOrder;
            }
        };
    }

    @Bean
    public ItemWriter itemWriter(){
        return new JpaItemWriterBuilder<MadeOrder>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step1")
                .<MadeOrder, MadeOrder>chunk(10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }
}
