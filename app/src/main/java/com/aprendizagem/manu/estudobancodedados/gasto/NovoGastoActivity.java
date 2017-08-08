package com.aprendizagem.manu.estudobancodedados.gasto;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class NovoGastoActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {

    private static final int EXISTING_VIAGEM_LOADER = 0;

    private Uri mCurrentViagemUri;

    private EditText textDestino;
    private EditText textLocalHospedagem;

    private Button salvarViagem;

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

        // Find all relevant views that we will need to read user input from
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
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
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
        int razaoViagem = mRazao;
        if (mCurrentViagemUri == null && TextUtils.isEmpty(localHospedagem) && TextUtils.isEmpty(destino)
                && mRazao == ViagemEntry.RAZAO_DESCONHECIDA) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ViagemEntry.COLUMN_DESTINO, destino);
        values.put(ViagemEntry.COLUMN_LOCAL_ACOMODACAO, localHospedagem);
        values.put(ViagemEntry.COLUMN_RAZAO, razaoViagem);

        if (mCurrentViagemUri == null) {

            Uri newUri = getContentResolver().insert(ViagemEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.falha_insercao),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.sucesso_insercao),
                        Toast.LENGTH_SHORT).show();
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
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                ViagemEntry._ID,
                ViagemEntry.COLUMN_DESTINO,
                ViagemEntry.COLUMN_LOCAL_ACOMODACAO,
                ViagemEntry.COLUMN_RAZAO};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentViagemUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int destinoColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_DESTINO);
            int localHospedagemColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_LOCAL_ACOMODACAO);
            int razaoColumnIndex = cursor.getColumnIndex(ViagemEntry.COLUMN_RAZAO);

            // Extract out the value from the Cursor for the given column index
            String destino = cursor.getString(destinoColumnIndex);
            String localHospedagem = cursor.getString(localHospedagemColumnIndex);
            int razao = cursor.getInt(razaoColumnIndex);

            // Update the views on the screen with the values from the database
            textDestino.setText(destino);
            textLocalHospedagem.setText(localHospedagem);

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (razao) {
                case ViagemEntry.RAZAO_LAZER:
                    mRazao = 1;
                    break;
                case ViagemEntry.RAZAO_TRABALHO:
                    mRazao = 2;
                    break;
                default:
                    mRazao = 0;
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        textDestino.setText("");
        textLocalHospedagem.setText("");
    }

}