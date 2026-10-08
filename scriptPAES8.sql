--Creación de la base de datos
CREATE DATABASE bdventas;

--Creas esquema
CREATE SCHEMA catalogo;

--Crear tabla de categoría
CREATE TABLE catalogo.categorias
(
	id_categoria SERIAL PRIMARY KEY,
	nombre VARCHAR (80) NOT NULL,
	descripcion VARCHAR (200) NOT NULL,
	estado SMALLINT NOT NULL DEFAULT 1

	CONSTRAINT chk_categoria_estado 
		CHECK (estado IN(0,1))
);

--Comprobación de registros con DEFAULT
INSERT INTO catalogo.categorias (nombre, descripcion)
VALUES ('cereales 2','Categoria adicional');


--Creación de tabla de marcas
CREATE TABLE catalogo.marcas
(
	id_marca SERIAL PRIMARY KEY,
	nombre VARCHAR (80) NOT NULL UNIQUE,
	pais_origen VARCHAR (80),
	estado SMALLINT NOT NULL DEFAULT 1

	CONSTRAINT chk_marca_estado
		CHECK(estado IN(0,1))
);

-- 1. comprobar el DEFAULT de estado
INSERT INTO catalogo.marcas (nombre, pais_origen) 
VALUES ('Toyota', 'Japón');

-- 2. comprobar la inserción de un estado válido (0)
INSERT INTO catalogo.marcas (nombre, pais_origen, estado) 
VALUES ('Ford', 'Estados Unidos', 0);

-- 3. comprobar que pais_origen permite NULL
INSERT INTO catalogo.marcas (nombre) 
VALUES ('Renault');

-- 1. falla la columna nombre es obligatoria (NOT NULL)
INSERT INTO catalogo.marcas (pais_origen, estado) 
VALUES ('Alemania', 1);

-- 2. falla el valor de estado no es válido según el CHECK(estado IN(0,1))
INSERT INTO catalogo.marcas (nombre, pais_origen, estado) 
VALUES ('Nissan', 'Japón', 5);

-- 3. falla por duplicidad (UNIQUE), si se ejecuta este código dos veces, 
-- la primera se hará, pero la segunda tira error.
INSERT INTO catalogo.marcas (nombre, pais_origen) 
VALUES ('Chevrolet', 'Estados Unidos');


--Creación de la tabla productos 
CREATE TABLE catalogo.productos
(
	id_producto SERIAL NOT NULL PRIMARY KEY,
	codigo VARCHAR(20) NOT NULL UNIQUE,
	nombre VARCHAR (100) NOT NULL, 
	descripcion VARCHAR (200),
	precio NUMERIC (10,2) NOT NULL,
	stock INTEGER NOT NULL DEFAULT 0,
	fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
	id_categoria INTEGER NOT NULL,
	id_marca INTEGER NOT NULL,
	estado SMALLINT NOT NULL DEFAULT 1,

	CONSTRAINT fk_producto_categoria
		FOREIGN KEY (id_categoria)
		REFERENCES catalogo.categorias(id_categoria),

	CONSTRAINT fk_producto_marca
		FOREIGN KEY (id_marca)
		REFERENCES catalogo.marcas(id_marca),

	CONSTRAINT chk_producto_precio
		CHECK (precio > 0),

	CONSTRAINT chk_producto_stock 
		CHECK (stock > 0),

	CONSTRAINT chk_producto_estado
		CHECK (estado IN (0,1))
)


-- Crear cinco registros de marcas
INSERT INTO catalogo.marcas (nombre, pais_origen) VALUES 
('Samsung', 'Corea del Sur'),
('Apple', 'Estados Unidos'),
('Sony', 'Japón'),
('LG', 'Corea del Sur'),
('Asus', 'Taiwán');

-- Crear cinco registros de categorias
INSERT INTO catalogo.categorias (nombre, descripcion) VALUES 
('Smartphones', 'Teléfonos móviles y accesorios'),
('Laptops', 'Computadoras portátiles de alto rendimiento'),
('Televisores', 'Pantallas Smart TV de última generación'),
('Audio', 'Equipos de sonido, auriculares y bocinas'),
('Electrodomésticos', 'Línea blanca y aparatos para el hogar');

-- Crear cinco registros de productos 
INSERT INTO catalogo.productos (codigo, nombre, descripcion, precio, stock, id_categoria, id_marca) VALUES 
('PRD-001', 'Galaxy S23 Ultra', 'Teléfono de gama alta 256GB', 1199.99, 20, 1, 1),
('PRD-002', 'MacBook Pro M2', 'Laptop para diseño y programación', 1999.00, 15, 2, 2),
('PRD-003', 'Bravia XR 65"', 'Smart TV OLED con 4K HDR', 1450.50, 10, 3, 3),
('PRD-004', 'Barra de Sonido SN4', 'Barra de sonido con subwoofer', 199.99, 30, 4, 4),
('PRD-005', 'ROG Strix G15', 'Laptop gaming 144Hz', 1250.00, 12, 2, 5);



SELECT id_categoria, nombre FROM catalogo.categorias;

SELECT id_marca, nombre FROM catalogo.marcas;


-- ver productos con precio mayor a $1000 ordenados del más caro al más barato
SELECT 
    nombre, 
    descripcion, 
    precio, 
    stock 
FROM catalogo.productos 
WHERE precio > 1000 AND stock > 0
ORDER BY precio DESC;





SELECT
    c.conname AS nombre_constraint,
    CASE c.contype
        WHEN 'p' THEN 'PRIMARY KEY'
        WHEN 'f' THEN 'FOREIGN KEY'
        WHEN 'u' THEN 'UNIQUE'
        WHEN 'c' THEN 'CHECK'
        WHEN 'x' THEN 'EXCLUSION'
        WHEN 'n' THEN 'NOT NULL'
        WHEN 't' THEN 'CONSTRAINT TRIGGER'
        ELSE c.contype::text
    END AS tipo_constraint,
    pg_get_constraintdef(c.oid, true) AS definicion
FROM pg_constraint AS c
WHERE c.conrelid = 'catalogo.marca'::regclass -- acá colocar el nombre del esquema y la tabla que se acaban de crear en clases, según su base de datos
ORDER BY tipo_constraint, nombre_constraint;




























