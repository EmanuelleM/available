package com.aprendizagem.manu.estudobancodedados.viagem;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract.ViagemEntry;

public class NovaViagemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_VIAGEM_LOADER = 0;

    private Uri mCurrentViagemUri;

    private EditText textDestino;
    private EditText textLocalHospedagem;

    private Button salvarViagem;

    private int mRazao = ViagemEntry.RAZAO_DESCONHECIDA;

    private boolean mViagemModificada = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mViagemModificada = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_viagem);

        Intent intent = getIntent();
        mCurrentViagemUri = intent.getData();

        if (mCurrentViagemUri == null) {
            setTitle(getString(R.string.nova_viagem));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editando_viagem));

            getLoaderManager().initLoader(EXISTING_VIAGEM_LOADER, null, this);
        }

        textDestino = (EditText) findViewById(R.id.edit_text_destino);
        textLocalHospedagem = (EditText) findViewById(R.id.input_local_hospedagem);

        salvarViagem = (Button) findViewById(R.id.button_salvar_viagem);
        salvarViagem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                salvarViagem();
            }
        });

        textDestino.setOnTouchListener(mTouchListener);
        textLocalHospedagem.setOnTouchListener(mTouchListener);

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_lazer:
                if (checked)
                    mRazao = 1;
                break;
            case R.id.radio_negocios:
                if (checked)
                    mRazao = 2;
                break;
        }
    }

    private void salvarViagem() {
        String destino = textDestino.getText().toString().trim();
        String localHospedagem = textLocalHospedagem.getText().toString().trim();
        String idDousuario = Constantes.getIdDoUsuario();
        int razaoViagem = mRazao;

        if (mCurrentViagemUri == null && TextUtils.isEmpty(localHospedagem) && TextUtils.isEmpty(destino)
                && mRazao == ViagemEntry.RAZAO_DESCONHECIDA) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ViagemEntry.COLUMN_DESTINO, destino);
        values.put(ViagemEntry.COLUMN_LOCAL_ACOMODACAO, localHospedagem);
        values.put(ViagemEntry.COLUMN_RAZAO, razaoViagem);
        values.put(ViagemEntry.COLUMN_ID_USUARIO, idDousuario);

        if (mCurrentViagemUri == null) {

            Uri newUri = getContentResolver().insert(ViagemEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.erro_salvar_viagem),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.viagem_salva),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NovaViagemActivity.this, ListaViagemActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentViagemUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.falha_update),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_sucesso),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ViagemEntry._ID,
                ViagemEntry.COLUMN_DESTINO,
                ViagemEntry.COLUMN_LOCAL_ACOMODACAO,
                ViagemEntry.COLUMN_RAZAO};

        return new CursorLoader(this,
                mCurrentViagemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        textDestino.setText("");
        textLocalHospedagem.setText("");
    }
}
