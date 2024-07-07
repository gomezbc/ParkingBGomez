# ParkingBGomez

## Descripción
Esta aplicación Android fue desarrollada como parte del Aula de Empresa LKS NEXT: Movilidad en la Facultad de Informática de San Sebastián de la Universidad del País Vasco (UPV/EHU). El proyecto se realizó entre el 07/03/2024 y el 04/07/2024.

### Objetivo de la Aplicación
El objetivo principal de esta aplicación es facilitar la gestión de reservas de plazas de aparcamiento. La aplicación permite a los usuarios registrar y acceder a sus cuentas, realizar y gestionar reservas de plazas de aparcamiento, recibir notificaciones sobre sus reservas y visualizar su historial de uso. Además, incluye funcionalidades específicas como la diferenciación entre tipos de plazas de aparcamiento (normales, con cargador eléctrico, para motos, etc.), y la integración con un backend en Firebase para la gestión de datos y autenticación. Esta aplicación busca proporcionar una solución eficiente y user-friendly para optimizar la experiencia de estacionamiento de los usuarios.

![app preview](https://github.com/gomezbc/ParkingBGomez/assets/77118356/14707673-f3e3-4b04-b981-743f91b2d2ca)

## Características
El temario del aula ha sido el siguiente:
- **Introducción al Prototipado UX**: La aplicación sigue principios de diseño UX para mejorar la experiencia del usuario.
- **Arquitectura y Ciclo de Vida en Android**: Implementación siguiendo las mejores prácticas de arquitectura y gestión del ciclo de vida.
- **Integración y Desarrollo Continuo**: Configurada para integración y desarrollo continuo.
- **Calidad con SonarCloud y Testing Unitario**: Utiliza SonarCloud para análisis de calidad y pruebas unitarias.
- **Testing Funcional con Espresso y UIAutomator**: Realización de pruebas funcionales utilizando Espresso y UIAutomator.

## Instalación
Sigue estos pasos para instalar y ejecutar la aplicación en tu entorno local:
1. Clona este repositorio: `git clone https://github.com/gomezbc/ParkingBGomez.git`
2. Abre el proyecto en Android Studio.
3. Sincroniza las dependencias del proyecto.
4. Ejecuta la aplicación en un emulador o dispositivo Android conectado.

>[!warning]
> Si quieres mostrar el mapa de google maps, debes de tener una cuenta de google y seguir los pasos que se indican en el siguiente enlace: [Google Maps API Key](https://developers.google.com/maps/documentation/embed/get-api-key)
> 
> Crea el fichero secrets.properties en la carpeta raíz del proyecto y añade la siguiente línea: `MAPS_API_KEY=TU_API_KEY`

## Desarrollo
### Tecnologías Usadas
- **Lenguaje**: Java/Kotlin
- **Arquitectura**: MVVM
- **Bibliotecas**:
  - Kover 
  - Firebase para Autenticación y BD
  - LiveData y ViewModel para la gestión del ciclo de vida
  - Espresso y UIAutomator para pruebas funcionales
