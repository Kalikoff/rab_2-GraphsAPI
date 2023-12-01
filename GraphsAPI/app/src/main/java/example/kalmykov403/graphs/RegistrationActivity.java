package example.kalmykov403.graphs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import example.kalmykov403.graphs.databinding.ActivityRegistrationBinding;
import example.kalmykov403.graphs.model.TokenDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding = null;

    private GraphAPI api = ApiBuilder.getAPI();

    private SharedPreferencesRepository repository;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this.getApplicationContext();
        repository = new SharedPreferencesRepository(context);

        if (!repository.getAccountName().isEmpty()) {
            api.sessionOpen(repository.getAccountName(), repository.getAccountSecret()).enqueue(new Callback<TokenDTO>() {
                @Override
                public void onResponse(Call<TokenDTO> call, Response<TokenDTO> response) {
                    repository.saveToken(response.body().token);

                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<TokenDTO> call, Throwable t) {

                }
            });
        } else {
            binding.buttonCreateAccount.setOnClickListener(v -> {
                if (!binding.etAccountName.getText().toString().isEmpty() && !binding.etAccountPassword.getText().toString().isEmpty()) {
                    api.accountCreate(binding.etAccountName.getText().toString(), binding.etAccountPassword.getText().toString()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            repository.saveAccountName(binding.etAccountName.getText().toString());
                            repository.saveAccountSecret(binding.etAccountPassword.getText().toString());
                            api.sessionOpen(repository.getAccountName(), repository.getAccountSecret()).enqueue(new Callback<TokenDTO>() {
                                @Override
                                public void onResponse(Call<TokenDTO> call, Response<TokenDTO> response) {
                                    repository.saveToken(response.body().token);

                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<TokenDTO> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
            });
        }
    }
}