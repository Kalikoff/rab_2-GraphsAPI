package example.kalmykov403.graphs;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import example.kalmykov403.graphs.databinding.ActivityMainBinding;
import example.kalmykov403.graphs.databinding.AddNodeDialogBinding;
import example.kalmykov403.graphs.model.GraphItem;
import example.kalmykov403.graphs.model.IdDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding = null;

    ArrayAdapter<GraphItem> graphsAdapter;
    private GraphAPI api = ApiBuilder.getAPI();
    private SharedPreferencesRepository repository;
    private MainActivity mainActivity;

    private boolean editEnable = false;
    private boolean deleteEnable = false;
    private boolean copyEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = new SharedPreferencesRepository(this.getApplicationContext());
        mainActivity = this;
        graphsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        binding.listGraphs.setAdapter(graphsAdapter);
        binding.listGraphs.setOnItemClickListener((parent, view, position, id) -> {
            if (!editEnable && !deleteEnable && !copyEnable) {
                Intent i = new Intent(getApplicationContext(), SecondActivity.class);
                i.putExtra("GRAPH_ID", graphsAdapter.getItem(position).id);
                startActivity(i);
            } else if (editEnable) {
                openEditGraphDialog(graphsAdapter.getItem(position).id);
                editEnable = false;
            } else {
                api.graphDelete(repository.getToken(), graphsAdapter.getItem(position).id).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        deleteEnable = false;
                        refreshGraphs();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {}
                });
            }

        });

        refreshGraphs();

        binding.buttonNew.setOnClickListener(v -> {
            openAddGraphDialog();
        });

        binding.buttonEdit.setOnClickListener(v -> {
            deleteEnable = false;
            editEnable = true;
            copyEnable = false;
        });

        binding.buttonDelete.setOnClickListener(v -> {
            deleteEnable = true;
            editEnable = false;
            copyEnable = false;
        });

        binding.buttonExit.setOnClickListener(v -> {
            api.sessionDelete(repository.getToken()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    mainActivity.finishAffinity();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        });
        binding.buttonDeleteAccount.setOnClickListener(v -> {
            api.accountDelete(repository.getToken()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    repository.saveAccountName("");
                    repository.saveAccountSecret("");
                    repository.saveToken("");
                    Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
                    startActivity(i);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        });
    }

    private void refreshGraphs() {
        graphsAdapter.clear();
        api.graphList(repository.getToken()).enqueue(new Callback<List<GraphItem>>() {
            @Override
            public void onResponse(Call<List<GraphItem>> call, Response<List<GraphItem>> response) {
                graphsAdapter.addAll(response.body());

                binding.buttonEdit.setEnabled(!graphsAdapter.isEmpty());
                binding.buttonDelete.setEnabled(!graphsAdapter.isEmpty());
            }

            @Override
            public void onFailure(Call<List<GraphItem>> call, Throwable t) {

            }
        });
    }

    private void openAddGraphDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();

        View dialogView = getLayoutInflater().inflate(R.layout.add_node_dialog, null);
        AddNodeDialogBinding dialogBinding = AddNodeDialogBinding.bind(dialogView);

        dialogBinding.tvTitle.setText(R.string.add_graph);

        dialogBinding.btnAdd.setOnClickListener(view -> {
            if (!dialogBinding.etName.getText().toString().isEmpty()) {
                api.graphCreate(repository.getToken(), dialogBinding.etName.getText().toString()).enqueue(new Callback<IdDTO>() {
                    @Override
                    public void onResponse(Call<IdDTO> call, Response<IdDTO> response) {
                        refreshGraphs();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<IdDTO> call, Throwable t) {}
                });
            }
        });
        dialog.setView(dialogView);
        dialog.getWindow().setLayout(300, 300);
        dialog.show();
    }

    private void openEditGraphDialog(Integer graphId) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();

        View dialogView = getLayoutInflater().inflate(R.layout.add_node_dialog, null);
        AddNodeDialogBinding dialogBinding = AddNodeDialogBinding.bind(dialogView);

        dialogBinding.tvTitle.setText(R.string.edit_graph);

        dialogBinding.btnAdd.setOnClickListener(view -> {
            if (!dialogBinding.etName.getText().toString().isEmpty()) {
                api.graphUpdate(repository.getToken(), graphId, dialogBinding.etName.getText().toString()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        refreshGraphs();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {}
                });
            }
        });
        dialog.setView(dialogView);
        dialog.getWindow().setLayout(300, 300);
        dialog.show();
    }
}