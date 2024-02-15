package sql_reuete;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
//conexion au serveur l
//clique sur base donnee l 
//connexion au base donner l
//clique sur exec
//exec
public class SQLTraining extends JFrame implements ActionListener {

    private JLabel lblServer, lblDB, lblTables, lblRequete, lblResultat;
    private JTextField txtServer, txtUsername, txtPassword;
    private JButton btnTables, btnConnectServer, btnConnectDB, btnExecute;
    private JComboBox<String> cmbDB;
    private JList<String> lstTables;
    private JTextArea txtRequete, txtResultat;
    private JScrollPane scrollRequete, scrollResult;
    private String requete, resultat, database, table;
    ArrayList<String> allDB = new ArrayList<String>();
    private JPanel pTable, pRequest, pResultat, pDB, pCentral;

    private Connection cnx;
    private Statement st;
    private ResultSet rs;

    public void connect() {
        String uid = txtUsername.getText();
        String mdp = txtPassword.getText();
        String server = txtServer.getText();
        String Pilote = "com.mysql.cj.jdbc.Driver";
        String Base = "jdbc:mysql://" + server + "/";

        try {
            Class.forName(Pilote);
            cnx = DriverManager.getConnection(Base, uid, mdp);
            System.out.println("Connexion avec le serveur " + server + " établie ");

            st = cnx.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = st.executeQuery("SHOW DATABASES");

            cmbDB.removeAllItems(); // Clear existing items in the ComboBox

            while (rs.next()) {
                cmbDB.addItem(rs.getString(1)); // Assuming the database name is in the first column
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Erreur de pilote : " + e);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur de BD : " + e);
        }
    }
//    button pour connecter au database specifique
    public void connectToDatabase(String selectedDatabase) {
        try {
            // Close the existing connection if any
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
            }

            // Establish a new connection to the selected database
            String uid = txtUsername.getText();
            String mdp = txtPassword.getText();
            String server = txtServer.getText();
            String Pilote = "com.mysql.cj.jdbc.Driver";
            String Base = "jdbc:mysql://" + server + "/" + selectedDatabase;

            Class.forName(Pilote);
            cnx = DriverManager.getConnection(Base, uid, mdp);
            System.out.println("Connexion avec la base " + selectedDatabase + " établie ");

            // Populate the list of tables in lstTables
            st = cnx.createStatement();
            rs = st.executeQuery("SHOW TABLES");

            DefaultListModel<String> tableListModel = new DefaultListModel<>();
            while (rs.next()) {
                tableListModel.addElement(rs.getString(1));
            }
            lstTables.setModel(tableListModel);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur : " + e);
        }
    }

//  por enter la requete et la executer 
      public void executeQuery(String query) {
            try {
                // Ensure a database connection is established
                if (cnx == null || cnx.isClosed()) {
                    System.out.println("No active database connection.");
                    return;
                }

                Statement statement = cnx.createStatement();

                if (query.toLowerCase().startsWith("select")) {
                    // SELECT query
                    rs = statement.executeQuery(query);
                    displayResultSet(rs);
                } else {
                    // UPDATE, INSERT, or DELETE query
                    int affectedRows = statement.executeUpdate(query);
                    displayUpdateResult(affectedRows);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur d'exécution de la requête : " + e);
            }
        }

        private void displayResultSet(ResultSet resultSet) throws SQLException {
            // Process and display the SELECT query result in txtResultat
            StringBuilder result = new StringBuilder();
            int columnCount = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(resultSet.getString(i)).append("\t");
                }
                result.append("\n");
            }

            txtResultat.setText(result.toString());
        }

        private void displayUpdateResult(int affectedRows) {
            // Display the number of affected rows for UPDATE, INSERT, or DELETE queries
            txtResultat.setText("Query executed successfully. Affected rows: " + affectedRows);
        }
//    --------------------------------------------------
    
    public SQLTraining() {
        super("SQL - Training");
        setSize(800, 400);

        pDB = new JPanel(new GridLayout(5, 2));
        lblServer = new JLabel("Serveur");
        txtServer = new JTextField("127.0.0.1");
        txtUsername = new JTextField("root");
        btnConnectDB = new JButton("Connecter DB");
        txtPassword = new JTextField("");
        btnConnectServer = new JButton("Connecter");
        lblDB = new JLabel("BAse de Denne");
        cmbDB = new JComboBox<String>();
        pDB.add(lblServer);
        pDB.add(txtServer);
        pDB.add(txtUsername);
        pDB.add(txtPassword);
        pDB.add(btnConnectServer);
        pDB.add(new JLabel());
        pDB.add(lblDB);
        pDB.add(new JLabel());
        pDB.add(cmbDB);
        pDB.add(btnConnectDB);
        this.add(pDB, BorderLayout.NORTH);

        pTable = new JPanel(new BorderLayout());
        lblTables = new JLabel("liste des tables");
        lstTables = new JList<String>();
        pTable.add(lblTables, BorderLayout.NORTH);
        pTable.add(lstTables);
        this.add(pTable, BorderLayout.WEST);

        pRequest = new JPanel(new BorderLayout());
        lblRequete = new JLabel("Requete");
        txtRequete = new JTextArea(5, 55);
        scrollRequete = new JScrollPane(txtRequete);
        pRequest.add(lblRequete, BorderLayout.NORTH);
        pRequest.add(scrollRequete);

        pResultat = new JPanel(new BorderLayout());
        lblResultat = new JLabel("Resultat");
        txtResultat = new JTextArea(10, 55);
        scrollResult = new JScrollPane(txtResultat);
        pResultat.add(lblResultat, BorderLayout.NORTH);
        pResultat.add(scrollResult);

        pCentral = new JPanel();
        pCentral.add(pRequest);
        btnExecute = new JButton("Executer");
        pCentral.add(new JLabel());
        pCentral.add(btnExecute);
        pCentral.add(pResultat);
        this.add(pCentral, BorderLayout.CENTER);
// btn pour connecter au SERVER ET AFFICHER LES DATABASE
        btnConnectServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });
        
//        btn pour connecter directement au database et pouvoir afficher les tables
        btnConnectDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDatabase = (String) cmbDB.getSelectedItem();
                connectToDatabase(selectedDatabase);
            }
        });
//  btn executer 
   
        btnExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = txtRequete.getText();
                executeQuery(query);
            }
        });

        this.setVisible(true);
    }

    public static void main(String[] args) {
        new SQLTraining();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Additional code for actionPerformed if needed
    }
}
