package br.com.desafio.philippemaia.bytebank;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class PagamentoMapper implements FieldSetMapper<Pagamento> {

    @Override
    public Pagamento mapFieldSet(FieldSet fieldSet) throws BindException {
        Pagamento pagamento = new Pagamento();
        pagamento.setNomeFuncionario(fieldSet.readString("nomeFuncionario"));
        pagamento.setCpf(fieldSet.readString("cpf"));
        pagamento.setAgencia(fieldSet.readString("agencia"));
        pagamento.setConta(fieldSet.readString("conta"));
        pagamento.setValor(fieldSet.readDouble("valor"));
        pagamento.setMesReferencia(fieldSet.readString("mesReferencia"));
        return pagamento;
    }
}
