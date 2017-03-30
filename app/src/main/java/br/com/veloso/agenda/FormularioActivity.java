package br.com.veloso.agenda;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.zip.Inflater;

import br.com.veloso.agenda.dao.AlunoDAO;
import br.com.veloso.agenda.modelo.Aluno;

public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    private FormularioHelper helper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);

        Intent intent = getIntent();
        final Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");

        if (aluno != null){
            helper.preencheFormulario(aluno);
        }

        Button botaoFoto = (Button) findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Faz a ação de tirar  uma foto
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Chave para outras Activitys souberem do que se trata Universal
                //Valor é uma URI que é onde vai ser armazenado a foto(caminho)

                caminhoFoto = getExternalFilesDir(null) + "/" +System.currentTimeMillis()+ ".jpg";
                File arquivoFoto = new File(caminhoFoto);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));

                startActivityForResult(intentCamera, CODIGO_CAMERA);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CODIGO_CAMERA){
                ImageView foto = (ImageView) findViewById(R.id.formulario_foto);
                //Vamos setar a foto no componente
                //Vamos usar o bitmap, precisamos converter para o byte a imagem
                //Temos que acessar o caminho da foto, então vamos declar como um atributo da classe
                Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
                //Reduzindo a foto
                //Parametros = foto, dimensao x dimensao, passar um filtro para não ficar borrada a imagem, pois ele retira pixels da foto.
                Bitmap bitmapReduzido = Bitmap.createScaledBitmap(bitmap, 300,300, true);
                foto.setImageBitmap(bitmapReduzido);
                //vamos  pedir para a foto preencher o imagemview por completo
                foto.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:
                Aluno aluno = helper.pegaAluno();
                Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() + "salvo!", Toast.LENGTH_SHORT).show();
                AlunoDAO dao = new AlunoDAO(this);

                if(aluno.getId() != null){
                    dao.altera(aluno);
                }else{
                    dao.insere(aluno);
                }


                //Assim que usar o DAO chamar o método close para fechar a conexão com o banco
                dao.close();

                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
