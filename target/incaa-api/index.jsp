<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>INCAA API</title>
    <style>
        body {
            padding: 25px 125px;
        }

        .codes {
            background-color: rgb(61, 60, 60);
            padding: 15px;
        }

        code {

            color: white;
        }
    </style>
</head>

<body>
    <h2>AINCAA API</h2>
    <p>Esta API fue creada para el curso de Codo a Codo Full Stack con Java.</p>
    <p>Consume datos desde una base de datos llamada incaa_db_omcn, creada por Osvaldo Corneli.</p>
    <h3>La API ofrece 4 endpoints:</h3>
    <ol>
        <li>PELICULAS</li>
        <li>ACTRICES</li>
        <li>ACTORES</li>
        <li>DIRECTORES</li>
    </ol>

    <div id="peliculas">
        <h3>PELICULAS</h3>
        <h3>incaa-api/peliculas</h3>
        <h3 id="cantidadpeliculas"></h3>
        <div class="codes">
            <code>
                {<br>
                    "title": "Moebius",<br>
                    "estreno": "1996-04-11",<br>
                    "duracion": "100 minutos",<br>
                    "sinopsis": "Un hombre obsesionado con un juego de mesa llamado \"Moebius\" se ve envuelto en una serie de eventos extraños y surrealistas que desafían la realidad.",<br>
                    "image": "https://pics.filmaffinity.com/moebius-556618515-mmed.jpg",<br>
                    "elenco": "Guillermo Angelelli, Lautaro Murúa, Luis Machín",<br>
                    "director": "Gustavo Mosquera",<br>
                    "trailer": "https://www.youtube.com/watch?v=sLY7hysjKFk",<br>
                    "wikiLink": "https://es.wikipedia.org/wiki/Moebius_(pel%C3%ADcula_de_1996)",<br>
                    "idPelicula": 1<br>
                  }<br>
        </code>
        </div>
    </div>
    <div id="actrices">
        <h3>ACTRICES</h3>
        <h3>incaa-api/actrices</h3>
        <h3 id="cantidadactrices"></h3>
        <div class="codes">
            <code>
            { <br>
            "id": 1, <br>
            "name": "Andrea Del Boca", <br>
            "description": "Conocida como \"la niña bonita del cine nacional\", Andrea Del Boca ha cautivado a las audiencias desde su debut infantil en \"El diario de Ana Frank\" (1969)...",<br>
            "wikiLink": "https://es.wikipedia.org/wiki/Andrea_del_Boca",<br>
            "image": "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Andrea_Del_Boca.jpg/800px-Andrea_Del_Boca.jpg"<br>
            }<br>
        </code>
        </div>
    </div>
    <div id="actores">
        <h3>ACTORES</h3>
        <h3>incaa-api/actores</h3>
        <h3 id="cantidadactores"></h3>
        <div class="codes">
            <code>
            {<br>
                "id": 1,<br>
                "name": "Ricardo Darín",<br>
                "description": "Ricardo Darín es uno de los actores argentinos más reconocidos a nivel internacional. Su versatilidad lo ha llevado a destacarse en una amplia gama de géneros, desde el drama hasta la comedia. Ha participado en numerosas películas aclamadas, como \"El secreto de sus ojos\" (2009) y \"Nueve reinas\" (2000), consolidándose como uno de los referentes del cine argentino.",<br>
                "wikiLink": "https://es.wikipedia.org/wiki/Ricardo_Dar%C3%ADn",<br>
                "image": "https://upload.wikimedia.org/wikipedia/commons/6/65/Marcozz.jpg"<br>
              }<br>
        </code>
        </div>
    </div>
    <div id="directores">
        <h3>DIRECTORES</h3>
        <h3>incaa-api/directores</h3>
        <h3 id="cantidaddirectores"></h3>
        <div class="codes">
            <code>
            {<br>
                "id": 1,<br>
                "name": "Lucrecia Martel",<br>
                "description": "Lucrecia Martel es una directora argentina reconocida internacionalmente por su estilo único y su enfoque en la narración visual. Sus películas, como 'La Ciénaga' (2001) y 'Zama' (2017), han sido aclamadas por la crítica y han ganado numerosos premios en festivales de cine alrededor del mundo.",<br>
                "wikiLink": "https://es.wikipedia.org/wiki/Lucrecia_Martel",<br>
                "image": "https://upload.wikimedia.org/wikipedia/commons/b/bf/Lucrecia_Martel_%28cropped%29.jpg"<br>
              }<br>
        </code>
        </div>
    </div>


    <h3>Actualizacion 18</h3>
</body>

</html>


<script>

    async function obtenerPeliculas() {
        try {
            const response = await fetch("http://localhost:8080/incaa-api/peliculas");
            const peliculas = await response.json();
            return peliculas;
        } catch (error) {
            console.error('Error al obtener las películas:', error);
        }
    }
    async function obtenerActores() {
        try {
            const response = await fetch("http://localhost:8080/incaa-api/actores");
            const actores = await response.json();
            return actores;
        } catch (error) {
            console.error('Error al obtener las actores:', error);
        }
    }
    async function obtenerActrices() {
        try {
            const response = await fetch("http://localhost:8080/incaa-api/actrices");
            const actrices = await response.json();
            return actrices;
        } catch (error) {
            console.error('Error al obtener las actrices:', error);
        }
    }
    async function obtenerDirectores() {
        try {
            const response = await fetch("http://localhost:8080/incaa-api/directores");
            const directores = await response.json();
            return directores;
        } catch (error) {
            console.error('Error al obtener las directores:', error);
        }
    }

    async function main() {
        const body = document.querySelector("body");
        const peliculas = await obtenerPeliculas();
        const actores = await obtenerActores();
        const actrices = await obtenerActrices();
        const directores = await obtenerDirectores();

        document.querySelector("#cantidadpeliculas").textContent = `Cantidad de peliculas ${peliculas.length}`
        document.querySelector("#cantidadactrices").textContent = `Cantidad de actrices ${actrices.length}`
        document.querySelector("#cantidadactores").textContent = `Cantidad de actores ${actores.length}`
        document.querySelector("#cantidaddirectores").textContent = `Cantidad de directores ${directores.length}`


    }

    main();

</script>