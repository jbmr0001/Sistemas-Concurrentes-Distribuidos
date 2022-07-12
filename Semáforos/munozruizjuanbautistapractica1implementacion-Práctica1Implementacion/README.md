[![logo](https://www.gnu.org/graphics/gplv3-127x51.png)](https://choosealicense.com/licenses/gpl-3.0/)
# Primera Práctica
Para la resolución de esta primera práctica se utilizará como herramienta de concurrencia los semáforos. Para el análisis y diseño el uso de semáforos es como se ha visto en las clases de teoría. Para la implementación se hará uso de la clase [`Semaphore`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Semaphore.html) de JAVA. Hay diferentes ejemplos en este [guión](https://gitlab.com/ssccdd/materialadicional/-/blob/master/README.md) donde se demuestra el uso general de la clase. Para la implementación se se utilizarán las interfaces [`Executors`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html) y [`ExecutorService`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) para la ejecución concurrente.

## Problema a resolver
Tenemos un sistema que dispone de una capacidad de recursos reutilizables quede deberán ser asignados a los procesos para que completen sus operaciones. El número de recursos estará determinado por la constante `NUM_RECURSOS` (20). Los elementos presentes para la solución serán los siguientes:

 - Un **monitor** para las peticiones de acceso a los recursos: Será el encargado de centralizar las peticiones que deben realizar los procesos y el gestor para operar con los recursos presentes en el sistema. Deberá satisfacer las siguientes restricciones.

	- Ningún proceso puede tener asignados más de 4 recursos.
    
	- Para la petición inicial de un proceso se le deben asignar siempre 2 recursos disponibles. Si no es posible se registrará el fallo correspondiente.
    
	- Si no se le pueden asignar más recursos se sustituirá la tarea que lleve más tiempo utilizando un recurso para ese proceso. Deberá registrarse esta sustitución.

- **Los procesos**: Para simular el comportamiento de los procesos en el sistema  se seguirán los siguientes pasos: 

	- Solicitará al monitor que se le asignen los recursos al inicio de sus operaciones. Inicialmente se le asignan dos recursos para las tareas 1 y 2 del proceso.
	
	- Realizará un número variable de ciclos de operaciones, entre 8 y 12. Ese número simula las operaciones que debe realizar el proceso para completar su ejecución.
    
	- Para cada operación se simulará que necesita un tiempo de entre 1 y 2 segundos:
    
	    -  Genera un número de tarea de entre las disponibles del proceso a la que se le asigna un recurso de los presentes en el sistema.
        
	    -  Pregunta al monitor si la tarea tiene asignado ya un recurso.
        
	    -  Si la tarea no tiene asignado un recurso se solicitará al monitor que le asigne un recurso.
        
	- Cuando se complete el último ciclo de operación finalizará el proceso. Antes de finalizar deberá comunicar al monitor que libera los recursos que tenga asignados.

- **Gestor de recursos**: Será el encargado de simular el comportamiento del gestor de recursos que seguirá los siguientes pasos:
	- Comprueba en el monitor las peticiones pendientes de liberación de recursos. La petición no puede bloquear al gestor de recursos si no hay peticiones pendientes.
		- Atiende todas las peticiones pendientes.
	
	- Comprueba en el monitor las peticiones pendientes de asignación de recursos que han realizado los procesos al monitor. La petición no puede bloquear al gestor de recursos si no hay peticiones pendientes.
		- Atiende una petición cada vez.

- **El sistema**: La simulación del comportamiento del sistema se realizará mediante el programa principal y tendrá los siguientes pasos:

	- Creará y ejecutará el gestor de recursos como primera tarea.
    
	- Creará y ejecutará un nuevo proceso en un intervalo comprendido entre 1 y 3 segundos. Para cada proceso se generará un número aleatorio de tareas que tiene un proceso y estará comprendido entre 4 y 8.
    
	- El tiempo de ejecución del sistema será de 2 minutos. Pasado ese tiempo finalizará los procesos que aún están activos y el gestor de procesos.
    
	- El sistema presentará los siguientes datos antes de finalizar:
		- Para cada proceso:
			- Tiempo de inicio.
			- Tiempo de ejecución.
			- Número de fallos en la asignación de recursos. Ya sea a su inicio o por sustitución para sus tareas.  
		- Mostrará el número de procesos que no han concluido su ejecución. 
		- La asignación media de recursos a los procesos.
		- La media de fallos de asignación de recursos a los procesos.

**NOTA**: El inicio del proceso es cuando solicita al monitor la asignación inicial de recursos. La finalización del proceso es cuando completa la solicitud al monitor para devolver los recursos asignados. Se deben asignar los recursos pedidos por los procesos, una vez iniciada su ejecución, es decir, si no hay disponibilidad se deberán satisfacer en cuanto haya disponibilidad. Se deberán definir todas las constantes simbólicas necesarias para la solución del problema. Para la solución no deben incluirse semáforos en el monitor.

## Solución

Se presenta la solución de la práctica dividida en las dos partes que se pedían, primero el análisis y posteriormente el diseño.

### Análisis

Se describirán las estructuras de datos, variables compartidas y procedimientos necesarios para comprender el diseño que se realiza de la práctica. 

### Datos

Tipos de datos necesarios para la solución de la práctica:

-   **TDA** `Lista<TipoElemento>`
	-   Almacena en orden FIFO elementos del `TipoElemento`    
	-   operaciones:
	-   `add(TipoElemento)` : inserta al final de la **Lista** un elemento.
    -   `remove()`: devuelve y elimina el primer elemento de la **Lista**.
	-   `find(TipoElemento)`: devolverá `ENCONTRADO` si el elemento se encuentra en la **Lista**. `NO_ENCONTRADO`  en otro caso.
	-  `size()`: nos devuelve el número de elementos en la **Lista**.
    
-   **TDA** `EstadoEjecucion`
	-   Enumerado (`INICIO`, `EJECUCION`)
    
-   **TDA** `Peticion`
	-   `idProceso : entero`. 
	-   `tipoPeticion : EstadoEjecucion`. 
	-   operaciones:
		-  `getID()`: devuelve `idProceso`.
		-  `getTipo()`: devuelve `tipoPeticion`.
    
-   **TDA** `BufferPeticiones` es una `Lista<Peticion>`

### Variables compartidas

Son los elementos de memoria a los que tendrán acceso tanto los procesos como el gestor de recursos. En la implementación se modelan mediante la clase `Monitor` que dispondrá de los métodos de acceso necesarios para ello.

Las variables compartidas entre `Proceso(id)` y `GestorRecursos` son las siguientes:

-   `bufferPeticiones : BufferPeticiones`. Almacenará las peticiones que se hacen del proceso al gestor de recursos para la asignación de un recurso disponible a una de las tareas del proceso.

-   `recursosDisponibles : entero`. Es una variable donde se almacenan los recursos que tiene el sistema disponible para que el gestor de recursos los asigne a los procesos que realicen peticiones para sus tareas.

-   `resultadoPeticion[idProceso] : Array de valor lógico` . Guarda el resultado de la petición de nuevos recursos por  hecha por el proceso y resuelta por el gestor.

### Semáforos

Los semáforos en la implementación también estarán agrupados en la clase `Monitor`. Para la resolución del ejercicio son los siguientes:

-   `maxPeticiones` . Representa el máximo de peticiones que se pueden almacenar en el `bufferPeticiones`. Se inicializa al tamaño de buffer.

-   `exmPeticiones`. Es necesario para garantizar el acceso seguro a `bufferPerticiones`. Se inicializa a 1.
    
-   `nuevaPeticion`. Representa las peticiones que se encuentran almacenadas en bufferPeticiones que deberán ser atendidas por el gestor de recursos. Se inicializa a 0.
    
-   `finAsignacion[idProceso]`. Es un array de semáforos, uno para cada proceso presente en el sistema, que permite al gestor de recursos comunicar al proceso, que realizó una petición, que ya se ha resuelto. Se inicializa a 0.
    
-   `exmRecurso`. Garantiza el acceso seguro a `recursosDisponibles`. Se inicializa a 1.

### Procedimientos de apoyo

Para el proceso del sistema (**hilo principal**) vamos a utilizar los siguientes procedimientos:

-   `crearProceso()`: Creará el proceso asociado al gestor de recursos o un proceso general del sistema con su `id` correspondiente. Los procesos creados se añadirán a una lista para su posterior finalización. El `id` asignado a un proceso será creciente empezando en 0.
    
-   `ejecutarProceso()`: Preparará para su ejecución el proceso que se pasa como parámetro.
    
-   `finalizarProcesos()`: Finalizará los procesos presentes en la lista de procesos creados por el sistema.
    
Para `Proceso(id)` vamos a utilizar los siguientes procedimientos:

-   `generarTareas()`: Generará un número entero que representa las tareas e ene un proceso.
    
-   `generarEjecuciones()`: Genera un número entero que representa el número de ciclos de ejecución que debe realizar un proceso antes de finalizar.
    
-   `nuevaTarea()`: Genera un número entero que representa el número de tarea que se utiliza en ese ciclo de ejecución del proceso.
    
Hay un procedimiento genérico para `Proceso(id)` y `GestorRecursos`:

-   `presentar()`: Este procedimiento presenta de forma legible por el usuario la variable que se le pase como parámetro.

## Diseño

Se presenta el diseño de la práctica mediante pseudocódigo donde se resolverá la ejecución necesaria para cada uno de los procesos implicados.

### Hilo Principal

```
crearProceso(gestorRecursos)

ejecutarProceso(gestorRecursos)

while ( No finalizacion ) {

	crearProceso(proceso(id))

	ejecutarProceso(proceso(id))

}

finalizarProcesos(listaProcesos)
```

### Proceso(id)

Variables locales
```
	id : entero
	tareaConRecurso : Lista de enteros  
	fallosRecurso : entero 
	totalTareas : entero
	tareaActual : entero
```
hilo de ejecución del proceso
```
	inicio()
	ejecucion()
	finalizacion()
```
Diseño de los módulos presentes en el hilo de ejecución del proceso:
#### inicio()
```
	// Inicializaciónentero
	fallosRecurso = NINGUNO
	totalTareas = generarTareas()

	// Solicitud para el inicio del proceso
	peticionGestor(INICIO)
	
	// Espera resolución
	esperaResolucion()

	// Actualiza los recursos asignados
	actualizarRecursos()
```
#### ejecucion()
```
	// Variables locales
	nuevoRecurso : valor lógico
	numEjecucionese : entero
	ejecucion : entero
	tareaActual : entero 

	ejecucion = PRIMERA // Inicio de la ejecución
	numEjecuciones = generaEjecuciones() // Total de ciclos de ejecución
	while ( ejecucion <= numEjecuciones ) {
		tareaActual = nuevaTarea() // Tarea que se ejecutará
		if ( tareaConRecurso.find(tareaActual) == NO_ENCONTRADO ) {
			fallosRecurso++
			
			// Solicitud de un recurso
			peticionGestor(EJECUCION)
			
			// Esperar resolución
			nuevoRecurso = esperaResolucion()

			// Actualiza los recursos asignados
			actualizaRecursos(nuevoRecurso, tareaActual)
		}

		ejecucion++  // Siguiente ejecución
	}
```
#### finalizacion()
Para que la implementación resulte más cómoda modifico la finalización para que sea el propio proceso el que retorne directamente los recursos al sistema sin la necesidad de la intermediación del gestor. 
```
	// Libera los recurso al proceso
	exmRecurso.wait()
	recursosDisponibles = recursosDisponibles + tareasConRecurso.size()
	exmRecurso.signal()
	presenta(fallosRecurso)
```
Ahora se presentan los módulos auxiliares utilizados en los módulos principales:
#### peticionGestor( estado : EstadoEjecucion )
```
	// Realiza una peticion al Gestor de Recursos
	// resolución productor consumidor
	maxPeticiones.wait()
	exmPeticiones.wait()
	bufferPeticiones.add(Peticion(id,estado))
	exmPeticiones.signal()
	nuevaPeticion.signal()
```
#### valor lógico : esperarResolucion()
```
	// Resolución de la petición por el Gestor de Recursos
	nuevoRecurso : valor lógico

	finAsignacion[id].wait() // Espera a que el Gestor resuelva
	nuevoRecurso = resutadoPeticion[id] // Resultado de la asignación

	return nuevoRecurso
```
#### actualizaRecursos()
```
 nuevoRecurso: valor lógico, tarea: entero, estado: EstadoEjecucion )
	if ( estado == INICIO ) {
		// Al inicio las dos primeras tareas tienen un recurso
	reaonadd(PRIMERA)
	tareaConRecurso.add(SEGUNDA)
```
#### actualizaRecursos( nuevoRecurso: valor lógico, tarea: entero )
```
	// En la ejecución se comprueba si hay que sustituir una tarea
	tareaConRecurso.add(tarea)
	if ( NO nuevoRecurso )
		// Se elimina la tarea más antigua con recurso
		t	tmareaCona.remove()
```

### Proceso GestorRecursos
Variables locales
```
	recursosProceso[idProceso] : Array de enteros
	fallosAsignacion : entero 
```
Se adapta el hilo de ejecución para que solo resuelva la petición de recursos  y el proceso será el encargado de devolver los recursos al sistema. De esta forma será más cómoda la implementación de la práctica.

hilo de ejecución del proceso
```
	// Variables locales
	peticion : Peticion
	fallosAsignacion = NINGUNO

	while ( NO interrumpido ) {
		peticion = obtenerPeticion()
		resolverPeticion(peticion)
	}

	presentar(fallosAsignacion)
```
Diseño de los módulos presentes en la ejecución del hilo del proceso:
#### Peticion : obtenerPeticion()
```
	// Variables locales
	peticion : Peticion

	// Obtener una petición de un proceso
	// solución productor consumidor
	nuevaPeticion.wait()
	exmPeticiones.wait()
	peticion = bufferPeticiones.remove()
	exmPeticiones.signal()
	maxPeticiones.signal()

	return peticion
```
#### resolverPeticion( peticion : Peticion )
```
	//Variables locales
	nuevoRecurso : valor lógico

	if ( peticion.getTipo() == INICIO ) {
		nuevoRecurso = obtenerRecurso(peticion)
		if ( No nuevoRecurso )
			posponer(peticion)
		else
			asignarRecurso(peticion, nuevoRecurso)
	} else {
		nuevoRecurso = obtenerRecurso(peticion)
		asignarRecurso(peticion, nuevoRecurso)
}
```
#### valor lógico : obtenerRecurso( peticion : Peticion )
```
	// Variables locales
	nuevoRecurso : valor lógico
	recursosAsignados : entero

	if ( peticion.getTipo() == INICIO ) {
		exmRecurso.wait()
		if ( recursosDisponibles >= MIN_RECURSOS ) {
			nuevoRecurso = DISPLONIBLE
			recursosDisponibles = recursosDisponibles - MIN_RECURSOS
			recursosProceso[peticion.getID()] = MIN_RECURSOS
		} else
			nuevoRecurso = No DISPONIBLE
			fallosRecurso++
		}
		exmRecurso.signal()
	} else {
		recursosAsignados = recursosProceso[peticion.getID()]
		exmRecurso.wait()
		if ((recursosDisponibles > NINGUNO) Y) AND (ecursosAsignados < MAXIMO_RECURSOS)){	
			recursosDisponibles--
			nuevoRecurso = DIPONIBLE
			recursosProceso[peticion.getID()] = recursosAsignados + 1
		} else {
			nuevoRecurso = No DISPONIBLE
			fallosRecurso++
		}
		exmRecurso.signal()
	}

	return nuevoRecurso	
```
#### posponer( peticion : Peticion )
```
	exmPeticiones.wait()
	bufferPeticiones.add(peticion)
	exmPeticiones.signal()
	nuevaPeticion.signal()
```
#### asignarRecursos( peticion : Peticion, nuevoRecurso : valor lógico )
```
	// Variables locales
	idProceso : entero

	// Comunicamos al proceso el resultado de la asignación de recurso
	idProceso = peticion.getID()
	resultadostaPeticion[idProceso] = nuevoRecurso
	finAsignacion[idProceso].signal()
```
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTM5OTM5MDMzNywtNjc1MjAwNDczLDIwNj
k2ODE2MTAsLTE2NDMwMDU0MTBdfQ==
-->
