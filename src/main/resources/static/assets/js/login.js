window.addEventListener('load', () => {

    /* PARTÍCULAS */ 
    particlesJS('main__particles', {
        particles: {
            number: {
                value: 80,
                density: {
                    enable: true,
                    value_area: 800
                }
            },
            color: {
                value: '#43A2CF' // Color rojo para simular el fuego
            },
            shape: {
                type: 'circle',
                stroke: {
                    width: 0,
                    color: '#000000'
                },
                polygon: {
                    nb_sides: 5
                }
            },
            opacity: {
                value: 0.7, // Opacidad de las partículas
                random: true,
                anim: {
                    enable: true,
                    speed: 1.2, // Velocidad de la animación de opacidad
                    opacity_min: 0,
                    sync: false
                }
            },
            size: {
                value: 3, // Tamaño de las partículas
                random: true,
                anim: {
                    enable: false,
                    speed: 40,
                    size_min: 0.1,
                    sync: false
                }
            },
            line_linked: {
                enable: false
            },
            move: {
                enable: true,
                speed: 4, // Velocidad de movimiento de las partículas
                direction: 'none',
                random: true,
                straight: false,
                out_mode: 'out',
                bounce: false,
                attract: {
                    enable: false,
                    rotateX: 600,
                    rotateY: 1200
                }
            }
        },
        interactivity: {
            events: {
                onhover: {
                    enable: false,
                    mode: 'repulse'
                }
            }
        },
        retina_detect: true
    });

    let urlParams = new URLSearchParams(window.location.search);
    let retryLogin = urlParams.get('retry');

    if (retryLogin == "true") {
        let error__login__container = document.querySelector('.error__login__container');
        if (error__login__container != null) {
            error__login__container.style.display = 'inline';
        } else {
            console.log("error__login__container no encontrado");
        }
    }
});