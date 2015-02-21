package gmerkley.writecsv;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class SurveyMain extends ActionBarActivity {

    private ArrayList<View> fields = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_main);
        fields = populateFieldsArray(fields);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBtnSubmit(View v) {
        String filename = null;
        try {
          filename = validateCSVExists();
        }
        catch (IOException e) {
            Log.e("IOException", "Unable to create CSV.");
        }
        if (filename != null) {
            try {
                if (validInputs(fields)) {
                    exportCSV(filename);
                }
            }
            catch (IOException e) {
                Log.e("IOException", "Unable to write to file");
            }
        }
    }

    private String validateCSVExists() throws IOException {
        try {
            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/Folder");

            boolean var = false;
            if (!folder.exists()) {
                var = folder.mkdir();
            }
            if (!var && !folder.isDirectory()) {
                throw new IOException("Unable to create csv file.");
            }
            else {
                Log.e("IOException", folder.toString());
                return folder.toString() + "/" + "Test.csv";
            }
        }
        catch (IOException e) {
            Log.e("IOException", "Unable to create directory:" + e.getCause());
            return null;
        }
    }

    private void exportCSV(final String filename) throws IOException {
        new Thread() {
            public void run() {
                try {
                    String outputString = "";
                    outputString = buildCSVString(outputString, fields);

                    FileWriter fw = new FileWriter(filename, true);
                    fw.append(outputString);
                    fw.append('\n');
                    fw.close();
                } catch (Exception e) {
                    Log.e("Writing", "Unable to write to file");
                }
            }
        }.start();
    }

    private String buildCSVString(String outputString, ArrayList<View> fields) {
        for (int counter = 0; counter < fields.size(); counter++) {
            if (fields.get(counter) instanceof EditText) {
                outputString +=  ((EditText)fields.get(counter)).getText().toString();
            }
            else if (fields.get(counter) instanceof RadioGroup) {
                int selectedRadio = ((RadioGroup)fields.get(counter)).getCheckedRadioButtonId();
                RadioButton radioIdentifier = (RadioButton) findViewById(selectedRadio);
                outputString += radioIdentifier.getText();
            }
            else {
                outputString += "Error";
            }
            if (counter + 1 < fields.size()) {
                outputString += ",";
            }
        }
        return outputString;
    }

    private ArrayList<View> populateFieldsArray(ArrayList<View> fields) {
        fields.add(fields.size(), (EditText) findViewById(R.id.txtName));
        fields.add(fields.size(), (RadioGroup) findViewById(R.id.rgHasContractor));
        return fields;
    }

    private boolean validInputs(ArrayList<View> fields) {
        final String REQUIRED_MSG = "Required";
        boolean validInputs = true;
        for (int counter = 0; counter < fields.size(); counter++) {
            if (fields.get(counter) instanceof EditText) {
                String input = ((EditText) fields.get(counter)).getText().toString().trim();
                if (input.length() == 0) {
                    ((EditText) fields.get(counter)).setError(REQUIRED_MSG);
                    validInputs = false;
                }
            }
            else if (fields.get(counter) instanceof RadioGroup) {
                int selectedRadio = ((RadioGroup)fields.get(counter)).getCheckedRadioButtonId();
                if (selectedRadio == -1) {
                    validInputs = false;
                }
            }
        }
        return validInputs;
    }
}
