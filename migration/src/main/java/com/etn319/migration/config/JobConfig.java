package com.etn319.migration.config;

import com.etn319.migration.model.Artist;
import com.etn319.migration.model.Song;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class JobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Autowired
    private MongoTemplate template;

    private final Map<String, String> artistCache = new HashMap<>();


    @Autowired
    public JobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                     @Qualifier("businessDataSource") DataSource dataSource
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcCursorItemReader<Song> songCursorReader() {
        return new JdbcCursorItemReaderBuilder<Song>()
                .name("songReader")
                .dataSource(dataSource)
                .sql("select * from songs")
                .rowMapper((rs, i) -> new Song())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Artist> artistCursorReader() {
        return new JdbcCursorItemReaderBuilder<Artist>()
                .name("artistReader")
                .dataSource(dataSource)
                .sql("select * from artists")
                .rowMapper((rs, i) ->
                        new Artist(rs.getLong("id"), rs.getString("name"), rs.getInt("birthyear"), null))
                .build();
    }

    @Bean
    public ItemProcessor<Artist, Artist> itemProcessor() {
        return artist -> artist;
    }

    @Bean
    public MongoItemWriter<Artist> artistMongoItemWriter() {
        return new MongoItemWriterBuilder<Artist>()
                .template(template)
                .collection("artists")
                .build();
    }

    @Bean
    public Job importArtistsJob(Step step) {
        return jobBuilderFactory.get("importArtistsJob")
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Начало job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("Конец job");
                    }
                })
                .build();
    }

    @Bean
    public Step step1(JdbcCursorItemReader<Artist> reader, MongoItemWriter<Artist> writer, ItemProcessor<Artist, Artist> processor) {
        return stepBuilderFactory.get("step1")
                .<Artist, Artist>chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(new ItemReadListener<>() {
                    public void beforeRead() {
                        log.info("Начало чтения");
                    }

                    public void afterRead(Artist o) {
                        log.info("Конец чтения");
                    }

                    public void onReadError(Exception e) {
                        log.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<Artist>() {
                    public void beforeWrite(List<? extends Artist> list) {
                        log.info("Начало записи");
                    }

                    public void afterWrite(List<? extends Artist> list) {
                        log.info("Конец записи \n{}", list);
                    }

                    public void onWriteError(Exception e, List<? extends Artist> list) {
                        log.info("Ошибка записи");
                    }
                })
                .listener(new ItemProcessListener<Artist, Artist>() {

                    public void beforeProcess(Artist o) {
                        log.info("Начало обработки");
                    }

                    public void afterProcess(Artist o, Artist o2) {
                        log.info("Конец обработки");
                    }

                    public void onProcessError(Artist o, Exception e) {
                        log.info("Ошбка обработки");
                    }
                })
                .listener(new ChunkListener() {
                    public void beforeChunk(ChunkContext chunkContext) {
                        log.info("Начало пачки");
                    }

                    public void afterChunk(ChunkContext chunkContext) {
                        log.info("Конец пачки");
                    }

                    public void afterChunkError(ChunkContext chunkContext) {
                        log.info("Ошибка пачки");
                    }
                })
                .build();
    }
}
