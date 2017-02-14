package br.com.veloso.agenda;

import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //parent = lista que clicamos
            //View = a view que clicamos o item
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);

                //Depois de clicado no item vamos abrir o formularioactivity para poder editar os dados
                Intent intentVaiProFromulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                //Precisamos mandar os dados do aluno clicado da lista para o FormularioActivity através do Intent
                //Para isso vamos usar o putExtra()
                //Usamos chave e valor, pois poderiamos passar mais coisas na intent, dessa forma fica definido o que vou querer recuperar pela chave "Aluno"
                //Passando o objeto Aluno ele não consegue enviar dessa forma
                intentVaiProFromulario.putExtra("aluno", aluno);
                startActivity(intentVaiProFromulario);

                Toast.makeText(ListaAlunosActivity.this, "Aluno " + aluno.getNome() + " Clicado", Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd = (Button) findViewById(R.id.lista_novoaluno);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentVaiProFormulario);
            }
        });

        //Criando um menu de contexto, para a Lista de Alunos
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

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        MenuItem itemSite = menu.add("Visitar site");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);

        String site = aluno.getSite();
        if(!site.startsWith("http://")){
            site = "http://" + site;
        }

        intentSite.setData(Uri.parse(site));
        //Uma forma melhor de fazer o click do item do botão para enviar para outra Activity
        itemSite.setIntent(intentSite);

        //criando o menu na mão
        //Guardar a referencia do menu click deletar
        MenuItem deletar = menu.add("Deletar");
        //Para pegar o clique do botão deletar
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //Chamar o método de deletar da classe AlunoDAO
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.deleta(aluno);
                dao.close();
                Toast.makeText(ListaAlunosActivity.this, "Aluno " + aluno.getNome() + " removido", Toast.LENGTH_SHORT).show();
                carregaLista();
                return false;
            }
        });
    }
}
