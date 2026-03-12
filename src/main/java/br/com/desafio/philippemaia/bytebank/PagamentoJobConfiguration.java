package br.com.desafio.philippemaia.bytebank;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PagamentoJobConfiguration {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public Job job(Step passoInicial, JobRepository jobRepository) {
        return new JobBuilder("pagamentos", jobRepository)
                .start(passoInicial)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step passoInicial(ItemReader<Pagamento> reader,
                             ItemWriter<Pagamento> writer,
                             JobRepository jobRepository){

        return new StepBuilder("passo-inicial", jobRepository)
                .<Pagamento, Pagamento>chunk(200, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<Pagamento> reader(){
        return new FlatFileItemReaderBuilder<Pagamento>()
                .name("leitura-csv")
                .resource(new FileSystemResource("files/dadosficticios.csv"))
//                .comments("Nome|CPF|Agência|Conta|Valor|Mês de Referência")
                .linesToSkip(1)
                .delimited()
                .delimiter("|")
                .names("nomeFuncionario", "cpf", "agencia", "conta", "valor", "mesReferencia")
                .fieldSetMapper(new PagamentoMapper())
                .build();
    }

    @Bean
    public ItemWriter<Pagamento> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Pagamento>()
                .dataSource(dataSource)
                .sql(
                    """
                         INSERT INTO pagamento (nome_funcionario, cpf, agencia, conta, valor, mes_referencia) VALUES
                                               (:nomeFuncionario, :cpf, :agencia, :conta, :valor, :mesReferencia)
                    """
                )
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

}
