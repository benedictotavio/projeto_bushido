package br.org.institutobushido.model.aluno.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

@Data
public class Faltas {

    public Faltas(String motivo, String observacao) {
        this.data = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        this.motivo = motivo;
        this.observacao = observacao;
    }

    private String data;
    private String motivo;
    private String observacao;

}