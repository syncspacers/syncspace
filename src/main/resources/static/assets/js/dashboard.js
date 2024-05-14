window.addEventListener('load', () => {

    // Agregamos los eventos para el botón de ordenación
    let ordenar__ligero = document.getElementById('order__ligero');
    ordenar__ligero.addEventListener('click', () => {
        // Obtener el NodeList de los elementos
        const nodeList = document.querySelectorAll('.values__value');

        // Convertir el NodeList en un array
        const array = Array.from(nodeList);

        // Función de comparación para ordenar por peso
        function sortBySize(a, b) {
            const sizeA = parseFloat(a.querySelector('.info__size').textContent.replace(',', '').replace(' MB', ''));
            const sizeB = parseFloat(b.querySelector('.info__size').textContent.replace(',', '').replace(' MB', ''));
            return sizeA - sizeB;
        }

        const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
        // Ordenar el array por peso
        array.sort(sortBySize);

        // Actualizar el orden en el DOM
        array.forEach(item => container.appendChild(item)); // Reemplaza '.container' con el selector del contenedor de los elementos
    });
    let ordenar__pesado = document.getElementById('order__pesado');
    ordenar__pesado.addEventListener('click', () => {
        // Obtener el NodeList de los elementos
        const nodeList = document.querySelectorAll('.values__value');

        // Convertir el NodeList en un array
        const array = Array.from(nodeList);

        // Función de comparación para ordenar por peso
        function sortBySizeDesc(a, b) {
            const sizeA = parseFloat(a.querySelector('.info__size').textContent.replace(',', '').replace(' MB', ''));
            const sizeB = parseFloat(b.querySelector('.info__size').textContent.replace(',', '').replace(' MB', ''));
            return sizeB - sizeA; // Orden descendente por peso
        }

        const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
        // Ordenar el array por peso
        array.sort(sortBySizeDesc);

        // Actualizar el orden en el DOM
        array.forEach(item => container.appendChild(item)); // Reemplaza '.container' con el selector del contenedor de los elementos
    });
    let ordenar__antiguo = document.getElementById('order__antiguo');
    let ordenar__reciente = document.getElementById('order__reciente');
    ordenar__antiguo.addEventListener('click', () => {
        // Obtener el NodeList de los elementos
        const nodeList = document.querySelectorAll('.values__value');

        // Convertir el NodeList en un array
        const array = Array.from(nodeList);

        const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
        // Función de comparación para ordenar por fecha en orden descendente
        function sortByDateDesc(a, b) {
            const dateA = new Date(a.querySelector('.info__date').textContent);
            const dateB = new Date(b.querySelector('.info__date').textContent);
            return dateB - dateA; // Orden descendente por fecha
        }

        // Ordenar el array por fecha en orden descendente
        array.sort(sortByDateDesc);
        // Actualizar el orden en el DOM
        array.forEach(item => container.appendChild(item));
    });

    ordenar__reciente.addEventListener('click', () => {
        // Obtener el NodeList de los elementos
        const nodeList = document.querySelectorAll('.values__value');

        // Convertir el NodeList en un array
        const array = Array.from(nodeList);

        // Función de comparación para ordenar por peso
        function sortByDateAsc(a, b) {
            const dateA = new Date(a.querySelector('.info__date').textContent);
            const dateB = new Date(b.querySelector('.info__date').textContent);
            return dateA - dateB; // Orden ascendente por fecha
        }
        
        const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
        // Función de comparación para ordenar por fecha en orden descendente
        // Ordenar el array por fecha en orden descendente
        array.sort(sortByDateAsc);
        // Actualizar el orden en el DOM
        array.forEach(item => container.appendChild(item));
    });

    // Programamos los filtros
    let copia__contenido = document.querySelectorAll('.values__value');
    let copia__actual = null;
    let filtros__ligeros = document.getElementById('filtros__ligero');
    let filtro_ligero_active = false;
    let filtro_pesado_active = false;
    filtros__ligeros.addEventListener('click', () => {
        // Convertir el NodeList en un array
        var array;
        if (copia__actual != null) {
            filtro_pesado_active = false;
            array = copia__actual;
        } else {
            array = Array.from(document.querySelectorAll('.values__value'));
        }

        if (filtro_pesado_active) {
            console.log("ACTIVADO 1");
            array = Array.from(copia__contenido);
            filtro_pesado_active = false;
        }

        function filterBySize(item) {
            const size = parseFloat(item.querySelector('.info__size').textContent.replace(',', '').replace(' MB', ''));
            return size < 600;
        }
        
        // Filtrar el array por archivos ligeros
        const filteredArray = array.filter(filterBySize);

        const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
        
        // Actualizar el orden en el DOM solo con los elementos filtrados
        container.innerHTML = ''; // Limpiar el contenedor antes de agregar los elementos filtrados
        filteredArray.forEach(item => container.appendChild(item));
        filtro_ligero_active = true;
    });
    let filtros__pesado = document.getElementById('filtros__pesado');
    filtros__pesado.addEventListener('click', () => {
        // Convertir el NodeList en un array
        var array;
        if (copia__actual != null) {
            console.log("ACTIVADO 2");
            array = copia__actual;
        } else {
            array = Array.from(document.querySelectorAll('.values__value'));
        }

        if (filtro_ligero_active) {
            array = Array.from(copia__contenido);
            filtro_ligero_active = false;
        }

        function filterBySizeUpper(item) {
            const size = parseFloat(item.querySelector('.info__size').textContent.replace(',', '').replace(' MB', ''));
            // 600 -> 60 megas
            return size >= 600;
        }
        
        // Filtrar el array por archivos ligeros
        const filteredArray = array.filter(filterBySizeUpper);

        const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
        
        // Actualizar el orden en el DOM solo con los elementos filtrados
        container.innerHTML = ''; // Limpiar el contenedor antes de agregar los elementos filtrados
        filteredArray.forEach(item => container.appendChild(item));
        filtro_pesado_active = true;
    });
    let filtros__reiniciar = document.getElementById('filtros__reiniciar');
    filtros__reiniciar.addEventListener('click', () => {
        let right__folders = document.querySelector('.right__folders');
        right__folders.style.background = '';
        copia_acarreada_files = null;
        copia_acarreada = null;
        // Obtener el NodeList de los elementos
        const nodeList = Array.from(copia__contenido);

        const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
        
        // Actualizar el orden en el DOM solo con los elementos filtrados
        container.innerHTML = ''; // Limpiar el contenedor antes de agregar los elementos filtrados
        const filteredArray = nodeList;
        filteredArray.forEach(item => container.appendChild(item));
    });

    // Copia de todos los archivos de dicho usuario
    var archivos = document.querySelectorAll('.values__value');

    // Agregamos los eventos del buscador
    let input__search = document.querySelector('.input__dashboard');
    input__search.addEventListener('input', (e) => {
        e.preventDefault();
        copia_acarreada = null;
        copia_acarreada_files = null;
        right__files.style.background = '';
        right__folders.style.background = '';
        let content__filter = input__search.value.toLowerCase().trim();
        // Convertir el NodeList en un array
        const array = Array.from(copia__contenido);

        function filterByName(item) {
            const name = item.querySelector('.value__title').textContent.trim().toLowerCase();
            return name.includes(content__filter);
        }
        
        // Filtrar el array por archivos ligeros
        const filteredArray = array.filter(filterByName);
        copia__actual = filteredArray;

        const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
        
        // Actualizar el orden en el DOM solo con los elementos filtrados
        container.innerHTML = ''; // Limpiar el contenedor antes de agregar los elementos filtrados
        filteredArray.forEach(item => container.appendChild(item));
    });
    // Agregamos los filtros para que solo muestra los archivos
    let right__files = document.querySelector('.right__files');
    let copia_acarreada_files = null;
    right__files.addEventListener('click', () => {
        // Convertir el NodeList en un array
        if (copia_acarreada != null) {
            // se pulso previamente las carpetas
            var array = Array.from(copia__contenido);
        } else {
            var array = Array.from(document.querySelectorAll('.values__value'));
        }
        var right = right__files.style.background;
        if (right == '') {
            // lo filtramos
            right__folders.style.background = "";
            right__files.style.background = "#ff77fd";
            function filterByName(item) {
                const name = item.dataset.type;
                return name === "file";
            }
            
            // Filtrar el array por archivos ligeros
            copia_acarreada_files = array;
            const filteredArray = array.filter(filterByName);
            copia__actual = filteredArray;
            const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
            
            // Actualizar el orden en el DOM solo con los elementos filtrados
            container.innerHTML = ''; // Limpiar el contenedor antes de agregar los elementos filtrados
            filteredArray.forEach(item => container.appendChild(item));
        } else {
            right__files.style.background = "";
            // Eliminamos el filtro
            // Obtener el NodeList de los elementos
            var nodeList;
            if (copia_acarreada_files != null) {
                nodeList = copia_acarreada_files;
            } else {
                nodeList = copia__contenido;
            }
            copia__actual = nodeList

            const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos

            // Actualizar el orden en el DOM solo con los elementos filtrados
            container.innerHTML = ''; // Limpiar el contenedor antes de agregar los elementos filtrados
            const filteredArray = nodeList;
            filteredArray.forEach(item => container.appendChild(item));
            right__folders.style.background = '';
        }
    });

    // Agregamos los filtros para que solo muestre las carpetas
    let right__folders = document.querySelector('.right__folders');
    let copia_acarreada = null;
    right__folders.addEventListener('click', () => {
        // Convertir el NodeList en un array
        if (copia_acarreada_files != null) {
            // se pulso previamente las carpetas
            var array = Array.from(copia__contenido);
        } else {
            var array = Array.from(document.querySelectorAll('.values__value'));
        }
        var right = right__folders.style.background;
        if (right == '') {
            // lo filtramos
            right__files.style.background = "";
            right__folders.style.background = "#ff77fd";
            function filterByName(item) {
                const name = item.dataset.type;
                return name === "folder";
            }
            
            // Filtrar el array por archivos ligeros
            copia_acarreada = array;
            const filteredArray = array.filter(filterByName);
            copia__actual = filteredArray;
            const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos
            
            // Actualizar el orden en el DOM solo con los elementos filtrados
            container.innerHTML = ''; // Limpiar el contenedor antes de agregar los elementos filtrados
            filteredArray.forEach(item => container.appendChild(item));
        } else {
            // Eliminamos el filtro
            // Obtener el NodeList de los elementos
            var nodeList;
            console.log(copia_acarreada);
            if (copia_acarreada != null) {
                nodeList = copia_acarreada;
            } else {
                nodeList = copia__contenido;
            }
            copia__actual = nodeList;
            const container = document.querySelector('.content__values'); // Reemplaza '.container' con el selector del contenedor de los elementos

            // Actualizar el orden en el DOM solo con los elementos filtrados
            container.innerHTML = ''; // Limpiar el contenedor antes de agregar los elementos filtrados
            const filteredArray = nodeList;
            filteredArray.forEach(item => container.appendChild(item));
            right__folders.style.background = '';
        }
    })

    // buscador
    let buscador__lupa = document.querySelector('.signup_login__lupa');
    // input buscador
    let input__dashboard = document.querySelector('.input__dashboard');
    buscador__lupa.addEventListener('click', () => {
        console.log("click en la lupa");
        if (input__dashboard.style.display == 'none' || input__dashboard.style.display == '') {
            ventana__icon.style.display = 'none';
            input__dashboard.style.display = 'flex';
        } else {
            input__dashboard.style.display = 'none';
        }
    });

    // Agregar carpetas
    let folder__icon = document.querySelector('.signup_login__add-folder');
    let ventana__icon = document.querySelector('.signup_login__form-folder');
    folder__icon.addEventListener('click', () => {
        if (ventana__icon.style.display == 'none' || ventana__icon.style.display == '') {
            input__dashboard.style.display = 'none';
            ventana__icon.style.display = 'block';
        } else {
            ventana__icon.style.display = 'none';
        }
    });

    ventana__icon.addEventListener('click', (e) => {
        e.stopPropagation();
    })

    // Subir ficheros
    let boton_upload = document.querySelector('.upgrade__button');
    let formulario_upload = document.querySelector('.dashboard__upload');
    boton_upload.addEventListener('click', () => {
        if (formulario_upload.style.display == '' || formulario_upload.style.display == 'none') {
            formulario_upload.style.display = 'block';
        } else {
            formulario_upload.style.display = 'none';
        }
    }); 

    let close_upload = document.querySelector('.upload-x');
    close_upload.addEventListener('click', () => {
        formulario_upload.style.display = 'none';
    }); 

    let cancel_upload = document.querySelector('.bottom-form__cancel');
    cancel_upload.addEventListener('click', (e) => {
        e.preventDefault();
        formulario_upload.style.display = 'none';
    });

    Array.from(archivos).forEach(function(valor, indice) {
        try {
            // boton de exit
            let value__exit = valor.querySelector('.information__exit');
            // cuadro con toda la informacion
            let value__information = valor.querySelector('.value__information');
            // comentario button y comentario form
            let comentario = valor.querySelector('.information__comentario');
            let comentario_form = valor.querySelector('.information__form__comentario');
            // cambiar nombre button y form
            let rename = valor.querySelector('.information__rename');
            let rename__form = valor.querySelector('.information__form');
            // boton de compartir y form
            let share = valor.querySelector('.information__share');
            let share__form = valor.querySelector('.share__types');
            // Botones publico privado y con amigos
            let boton_publico = valor.querySelector('.share__public');
            let boton_privado = valor.querySelector('.share__private');
            let boton_friends = valor.querySelector('.share__friends');

            let form_publico = valor.querySelector('.share__types--active');
            let form_privado = valor.querySelector('.share__types--private');
            let form_friends = valor.querySelector('.share__types--friends');
            // Boton de borrar archivo
            let boton_borrar = valor.querySelector('.trash-item');
            let borrar_ventana = valor.querySelector('.value__trash--form');
            let no_borrar = valor.querySelector('.trash--form__cancel');
            // Hacemos que al dar clic en el input del enlace en caso de que haya contenido
            // que lo notifique
            let enlace__publico = valor.querySelector('.types__enlace');
            let enlace__privado = valor.querySelector('.types__enlace__private');
            let enlace__friends = valor.querySelector('.types__enlace__friends');
            let publico = valor.querySelector('.enlace__public');
            let privado = valor.querySelector('.enlace__privado');
            let friends = valor.querySelector('.enlace__friends');
            // checkbutton
            let checkbutton__public = valor.querySelector('.checkbutton__public');
            let checkbutton__private = valor.querySelector('.checkbutton__private');
            checkbutton__public.addEventListener('click', (e) => {
                e.stopPropagation();
            })
            checkbutton__private.addEventListener('click', (e) => {
                e.stopPropagation();
            })
            enlace__publico.addEventListener('click', (e) => {
                // Copia el contenido del input
                console.log('clic en el enlace public');
                publico.select();
                document.execCommand('copy');
                e.stopPropagation();
            })

            enlace__privado.addEventListener('click', (e) => {
                // Copia el contenido del input
                console.log('clic en el enlace privado');
                privado.select();
                document.execCommand('copy');
                e.stopPropagation();
            })

            enlace__friends.addEventListener('click', (e) => {
                // Copia el contenido del input
                console.log('clic en el enlace friends');
                friends.select();
                document.execCommand('copy');
                e.stopPropagation();
            })
            // FIN DEL COPY DE LOS ENLACES
            no_borrar.addEventListener('click', (e) => {
                console.log("CLICKED");
                borrar_ventana.style.display = 'none';
                e.preventDefault();
                e.stopPropagation();
            })
            borrar_ventana.addEventListener('click', (e) => {
                e.stopPropagation();
            })
            boton_borrar.addEventListener('click', (e) => {
                console.log("BORRAR ARCHIVO");
                if (borrar_ventana.style.display == '' || borrar_ventana.style.display == 'none') {
                    // equivale a darle al exit
                    rename__form.style.display = 'none';
                    comentario_form.style.display = 'none';
                    share__form.style.display = 'none';
                    value__information.style.display = 'none';
                    form_friends.style.display = 'none';
                    form_privado.style.display = 'none';
                    form_publico.style.display = 'none';
                    borrar_ventana.style.display = 'block';
                } else {
                    // Cerramos todos los formularios
                    borrar_ventana.style.display = 'none';
                }
                e.stopPropagation();
            });
            boton_publico.addEventListener('click', (e) => {
                // Cerramos los posibles forms
                form_friends.style.display = 'none';
                form_privado.style.display = 'none';
                // Abrimos el publico
                form_publico.style.display = 'flex';
                e.stopPropagation();
            }); 
            boton_privado.addEventListener('click', (e) => {
                // Cerramos los posibles forms
                form_friends.style.display = 'none';
                form_publico.style.display = 'none';
                // Abrimos el publico
                form_privado.style.display = 'flex';
                e.stopPropagation();
            });
            boton_friends.addEventListener('click', (e) => {
                // Cerramos los posibles forms
                form_privado.style.display = 'none';
                form_publico.style.display = 'none';
                // Abrimos el publico
                form_friends.style.display = 'flex';
                e.stopPropagation();
            });
            share.addEventListener('click', (e) => {
                console.log("SHARE");
                if (share__form.style.display == '' || share__form.style.display == 'none') {
                    share__form.style.display = 'flex';
                } else {
                    // Cerramos todos los formularios
                    form_privado.style.display = 'none';
                    form_publico.style.display = 'none';
                    form_friends.style.display = 'none';
                    share__form.style.display = 'none';
                }
                e.stopPropagation();
            });
            rename.addEventListener('click', (e) => {
                console.log("CLICK rename");
                if (rename__form.style.display == '' || rename__form.style.display == 'none') {
                    // Cerramos por si esta abierto el input del comentario
                    comentario_form.style.display = 'none';
                    rename__form.style.display = 'flex';
                } else {
                    rename__form.style.display = 'none';
                }
                e.stopPropagation();
            });
            value__exit.addEventListener('click', (e) => {
                // Cerramos todas las ventanas
                rename__form.style.display = 'none';
                comentario_form.style.display = 'none';
                share__form.style.display = 'none';
                value__information.style.display = 'none';
                form_friends.style.display = 'none';
                form_privado.style.display = 'none';
                form_publico.style.display = 'none';
                console.log("CLICK EXIT");
                e.stopPropagation();
            })
            valor.addEventListener('contextmenu', (e) => {
                e.preventDefault();
                // Cerramos si esta abierto la ventana de borrar
                borrar_ventana.style.display = 'none';
                // Abrimos el menu de opciones
                value__information.style.display = 'flex'; 
            });
            valor.addEventListener('click', (e) => {
                e.preventDefault();
                value__information = valor.querySelector('.value__information');
                borrar_ventana = valor.querySelector('.value__trash--form');
                if ((value__information.style.display == '' || value__information.style.display == 'none') && (borrar_ventana.style.display == '' || borrar_ventana.style.display == 'none')) {
                    console.log("entramos en la carpeta");
                }
            });
            comentario.addEventListener('click', (e) => {
                console.log("abrir comentario");
                if (comentario_form.style.display == '' || comentario_form.style.display == 'none') {
                    // Cerramos por si esta abierto el input del rename
                    rename__form.style.display = 'none';
                    comentario_form.style.display = 'flex';
                } else {
                    comentario_form.style.display = 'none';
                }
                e.stopPropagation();
            })

        } catch (e) {
            console.log(e);
        }
        
    })
});