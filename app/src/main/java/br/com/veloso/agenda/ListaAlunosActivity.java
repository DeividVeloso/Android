package br.com.veloso.agenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import br.com.veloso.agenda.dao.AlunoDAO;
import br.com.veloso.agenda.modelo.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    private Button btnAdd;
    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);
        btnAdd = (Button) findViewById(R.id.lista_novoaluno);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentVaiProFormulario);
            }
        });

        //Criando um menu de contexto
        registerForContextMenu(listaAlunos);
    }

    private void carregaLista() {
        //Buscando alunos do banco de dados
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();

        //ArrayAdpter de Alunos
        ArrayAdapter<Aluno> adapter = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunos);
        listaAlunos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    //Criando um menu de contexto
    //Recebe um menu,View, e Informação do clique
    //menuInfo é quem diz que item da lista foi clicado
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        //criando o menu na mão
        //Guardar a referencia do menu click deletar
        MenuItem deletar = menu.add("Deletar");
        //Para pegar o clique do botão deletar
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //Para pegar a informação da lista, primeiro precisamos dizer que esse menuInfo é um menu de um Adapter
                //Só estou convertendo meu menu Info par aum menu mais especifico de adpter
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                return false;
            }
        });
    }
}
