window.addEventListener('load', () => {

    window.addEventListener('scroll', () => {
        var nuevaAltura = window.scrollY;
        let header = document.querySelector('.header');
        if (nuevaAltura > 120) {
            header.classList.add('header__fix');
        } else {
            header.classList.remove('header__fix');
        }
    }); 

    let upgrade__button = document.querySelector('.upgrade__button');
    upgrade__button.addEventListener('click', () => {

        document.getElementById("plans").scrollIntoView({behavior: 'smooth'})
    });

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
                value: '#43A2CF' // Color azul para simular los meteoritos
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
});