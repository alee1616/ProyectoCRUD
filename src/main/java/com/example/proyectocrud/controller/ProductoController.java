package com.example.proyectocrud.controller;

import com.example.proyectocrud.database.ConexionBD;
import com.example.proyectocrud.model.Categoria;
import com.example.proyectocrud.model.Marca;
import com.example.proyectocrud.model.Producto;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ProductoController {

    // Controles de la gestión
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<Categoria> cmbCategoria;
    @FXML
    private ComboBox<Marca> cmbMarca;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtDescripcion;


    // Botones de gestión
    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;


    //  Controles de búsqueda y filtros
    @FXML
    private TextField txtBuscarCodigoNombre;

    @FXML
    private ComboBox<Categoria> cmbBuscarCategoria;

    @FXML
    private DatePicker dpBuscarDesde;

    @FXML
    private DatePicker dpBuscarHasta;

    @FXML
    private TextField txtBuscarPrecioMin;

    @FXML
    private TextField txtBuscarPrecioMax;

    @FXML
    private CheckBox chkSoloExistencias;

    @FXML
    private ComboBox<String> cmbOrdenarPor;

    // Botones de búsqueda
    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiarFiltros;


    // Controles de la Tabla
    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, String> colDescripcion;

    @FXML
    private TableColumn<Producto, String> colMarca;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private TableColumn<Producto, LocalDate> colFecha;

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();


    @FXML
    public void initialize(){
        //Configuración TableView

    }

    private void configurarTableView() {
        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCodigo())
        );

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre())
        );

        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategoria())
        );

        colMarca.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMarca())
        );
        colDescripcion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescripcion())
        );
        colPrecio.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrecio()).asObject()
        );

        colStock.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getStock()).asObject()
        );

        colFecha.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getFechaCreacion())
        );

        tblProductos.setItems(listaProductos);
    }

    private void configurarSeleccionTableView(){
        tblProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {;
                    if (newValue != null) {
                        cargarProductoEnFormulario(newValue);
                    }
                });

    }


    /**
     * Recibe como parámetro un producto seleccionado del TableView
     * @param p, es el producto seleccionado del TableView
     *
     */

    @FXML
    private void cargarProductoEnFormulario(Producto p) {
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        dpFecha.setValue(p.getFechaCreacion());


        /**
         *  Buscamos dentro del ComboBox el valor que corresponde al ID
         */

        for (Categoria categoria: listaCategorias) {
            if (categoria.getIdCategoria() == p.getIdCategoria()) {
                cmbCategoria.setValue(categoria);
                break;
            }
        }

        /**
         * Buscamos dentro del ComboBox el valor que corresponde al ID
         */

        for (Marca marca: listaMarcas) {
            if (marca.getIdMarca() == p.getIdMarca()) {
                cmbMarca.setValue(marca);
                break;
            }

        }

    }

    /**
     * Consulta la categoría activas de la base de datos para mostrarlas dentro del comboBox
     */
    /**
     * Consulta la categoría activas de la base de datos para mostrarlas dentro del comboBox
     */
    private void cargarCategorias(){
        listaCategorias.clear();
        String sql = "SELECT id_categoria, nombre FROM catalogo.categorias WHERE estado = 1 ORDER BY nombre DESC";

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()){
                Categoria categoria = new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre")
                );
                listaCategorias.add(categoria);
            }

            /*
             * Utilizamos copias de la lista para evitar que
             * cada ComboBox tenga problemas de elección.
             */
            cmbCategoria.setItems(FXCollections.observableArrayList(listaCategorias));
            cmbBuscarCategoria.setItems(FXCollections.observableArrayList(listaCategorias));

            // Falta este bloque catch para solucionar el error
        } catch (SQLException e) {
            mostrarErrorBaseDatos(
                    "No se pudieron cargar las categorías desde la base de datos.", e
            );
        }
    }

    /**
     * Consulta las marcas activas de la base de datos para mostrarlas dentro del comboBox
     */
    private void cargarMarcas(){
        listaMarcas.clear();
        String sql = "SELECT id_marca, nombre FROM catalogo.marcas WHERE estado = 1 ORDER BY nombre DESC";

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()){
                Marca marca = new Marca(
                        rs.getInt("id_marca"),
                        rs.getString("nombre")
                );
                listaMarcas.add(marca);
            }

            cmbMarca.setItems(FXCollections.observableArrayList(listaMarcas));

        } catch (SQLException e) {
            mostrarErrorBaseDatos(
                    "No se pudieron cargar las marcas desde la base de datos.", e
            );
        }
    }



    /**
     *
     *
     */

    private void cargarOpcionesOrden(){

        cmbOrdenarPor.setItems
                (FXCollections.observableArrayList(
                        "Nombre",
                        "Precio menor a mayor",
                        "Precio mayor a menor",
                        "Fecha más reciente",
                        "Fecha más antigua",
                        "Stock mayor a menor ",
                        "Stock mwnor a mayor"
                )
        );
        cmbOrdenarPor.setValue("Nombre");
    }

    /**
     * Muestra una ventana de error al usuario cuando falla la base de datos
     */
    private void mostrarErrorBaseDatos(
            String mensaje,
            SQLException e
    ){
        e.printStackTrace();

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error de base de datos",
                mensaje
                        +"\n\n"
                        +"Detalle: "
                        + e.getMessage()
        );
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ){

        Alert alert =
                new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void guardarProducto() {

        if (!validarFormulario()) {
            return;
        }

        String codigo = txtCodigo.getText().trim();

        /*
         * Validación preventiva de UNIQUE.
         */
        if (existeCodigo(codigo, null)) {

            mostrarAlerta(
                    javafx.scene.control.Alert.AlertType.WARNING,
                    "Código duplicado",
                    "El código " + codigo +
                            " ya pertenece a otro producto."
            );

            txtCodigo.requestFocus();

            return;
        }

        String sql = """
                INSERT INTO catalogo.productos
                (
                    codigo,
                    nombre,
                    descripcion,
                    precio,
                    stock,
                    fecha_creacion,
                    id_categoria,
                    id_marca,
                    estado
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)
                """;

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, codigo);
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtDescripcion.getText().trim());
            ps.setBigDecimal(4, new java.math.BigDecimal(txtPrecio.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtStock.getText().trim()));

            ps.setDate(6, java.sql.Date.valueOf(dpFecha.getValue()));

            ps.setInt(7, cmbCategoria.getValue().getIdCategoria());
            ps.setInt(8, cmbMarca.getValue().getIdMarca());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {

                mostrarAlerta(
                        javafx.scene.control.Alert.AlertType.INFORMATION,
                        "Registro guardado",
                        "El producto fue registrado correctamente."
                );

                limpiarFormulario();

                cargarProductos();
            }

        } catch (SQLException e) {

            if ("23505".equals(e.getSQLState())) {

                mostrarAlerta(
                        javafx.scene.control.Alert.AlertType.WARNING,
                        "Registro duplicado",
                        "El código ingresado ya existe."
                );

            } else {
                mostrarErrorBaseDatos(
                        "No se pudo guardar el producto.",
                        e
                );
            }
        }
    }

    private void limpiarFormulario() {
    }

    private boolean existeCodigo(String codigo, Object o) {
    }

    private boolean validarFormulario() {
    }

    private void cargarProductos() {
        listaProductos.clear();

        String sql = "SELECT p.id_producto, p.codigo, p.nombre, p.descripcion, p.precio, p.stock, p.fecha_creacion, " +
                "c.id_categoria, c.nombre AS nombre_categoria, " +
                "m.id_marca, m.nombre AS nombre_marca " +
                "FROM catalogo.productos p " +
                "INNER JOIN catalogo.categorias c ON p.id_categoria = c.id_categoria " +
                "INNER JOIN catalogo.marcas m ON p.id_marca = m.id_marca " +
                "WHERE p.estado = 1 " +
                "ORDER BY p.nombre ASC";

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {

                // los 12 parámetros
                Producto producto = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getDate("fecha_creacion").toLocalDate(),
                        rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria"),
                        rs.getInt("id_marca"),
                        rs.getString("nombre_marca"),
                        1 // Estado activo
                );

                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            mostrarErrorBaseDatos(
                    "No se pudieron cargar los productos.",
                    e
            );
        }
    }


    

    //  Métodos OnAction

    @FXML
    void onNuevo(ActionEvent event) {
    }

    @FXML
    void onGuardar(ActionEvent event) {
    }

    @FXML
    void onActualizar(ActionEvent event) {
    }

    @FXML
    void onEliminar(ActionEvent event) {
    }

    @FXML
    void onBuscarAction(ActionEvent event) {
    }

    @FXML
    void onLimpiarFiltrosAction(ActionEvent event) {
    }

}