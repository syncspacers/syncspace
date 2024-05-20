window.addEventListener('load', () => {
    try {
        let close__password = document.querySelector('.password__exit--salida');
        let formulario__password = document.querySelector('.right__form--password');
        close__password.addEventListener('click', () => {
            if (formulario__password.style.display == '' || formulario__password.style.display == 'none') {
                formulario__password.style.display = 'flex';
            } else {
                formulario__password.style.display = 'none';
            }
        });
        let right__key = document.querySelector('.right__key');
        right__key.addEventListener('click', (e) => {
            formulario__password.style.display = 'flex';
            e.preventDefault();
            e.stopPropagation();
        });
    } catch (e) {
        console.log(e);
    }
});