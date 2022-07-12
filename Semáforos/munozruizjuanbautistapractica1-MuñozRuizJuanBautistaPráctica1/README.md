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

## Análisis y Diseño
Estructuras de datos, variables compartidas y procedimientos necesarios.
### Constantes
- `MAX_NUM_RECURSOS_ASIGNADOS(4): Entero` Entero con el número máximo de recursos que puede tener asignado un proceso.
- `MAX_NUM_RECURSOS_ASIGNAR(20): Entero` Entero con el número máximo de recursos que se pueden asignar.
- `MIN_OPERACIONES_RECURSO(8): Entero` Entero con el número mímino de tareas que realiza un proceso.
- `MAX_OPERACIONES_RECURSO(12): Entero` Entero con el número máximo de tareas que realiza un proceso.
- `ESTADO: Enum` Puede ser INICIAL O EJECUCIÓN.
### Variables compartidas
- `exmListaPeticionesLiberación(1): Semáforo de exclusión mutua.`
	Semáforo para garantizar la exclusión mutua en la lista de procesos pendientes de liberación de recursos. Compartido por Proceso y Gestor.
- `exmListaPeticionesAsignación(1): Semáforo de exclusión mutua.`
    Semáforo para garantizar la exclusión mutua en la lista de procesos pendientes de asignación de recursos así como a la lista de recursos. Compartido por Proceso y Gestor.
- `sincronizaciónInicial(0): Semáforo de sincronización.`
    Semáforo para garantizar la sincronización para que no se pase del punto inicial hasta que se le hayan asignado los 2 recursos iniciales. Compartido por Proceso y Gestor.
- `sincronizaciónEjecucion(0): Semáforo de sincronización.`
    Semáforo para garantizar que un proceso no pase de ese punto hasta que se le haya asignado 1 recurso. Compartido por Proceso y Gestor.

Los métodos de la clase monitor y sus estructuras de datos serán compartidos entre Proceso y Gestor ya que lo pasaremos por parámetro. Los métodos complejos del Monitor se han implementado como métodos públicos, mientras que para la inserción en las listas del monitor se ha supuesto un acceso a las estructuras de datos de este de manera pública por parte de Proceso o Gestor.
### Tipos de datos
#### Proceso(Monitor monitor)
- Variables locales:
    - `id: Entero` Identificador que permite identificar un Proceso de otro. 
    - `numFallos: Entero` Número de fallos de asignación de recursos.
    - `numMaxTareas: Entero` Número de tareas a realizar el proceso.
    - `estado: Estado` Puede ser INICIAL o EJECUCIÓN
    - `recursosAsignados: Entero` Número de recursos asignados.
    - `tareasPorHacer: Entero` Número de tareas por hacer.
    - `tareaActual: Entero` Id de la tarea actual.
    - `listaIDTarea: Lista<Entero>` Lista para guardar los id de las tareas de este proceso.
	
- Funciones:
     ```
	 func constructor
		this.id=unIdUnico()
		this.tareasPorHacer=random((MAX_OPERACIONES_RECURSO-MIN_OPERACIONES_RECURSO)+MIN_OPERACIONES_RECURSO)
		this.numFallos=0
		for i until tareasPorHacer //generamos los id de tareas de forma única
	        listaIdTarea=i+this.id*100
	    fin for
	  Fin func
	```
    ```
	 func ejecucion
		exmListaPeticionesAsignación.wait()
		monitor.listaPeticionesAsignacion.add(this) //realizamos petición
		exmListaPeticionesAsignación.signal()
		sincronizaciónInicial.wait() //esperamos hasta que nos asignen los recursos iniciales
		this.estado=ESTADO.INICIAL
		int recurso=monitor.asignarRecursos(this)
		this.recursosAsignados=this.recursosAsignados+recurso
		int tareasHechas=0
	    
		while this.tareasHechas < this.tareasPorHacer
		    if this.estado==ESTADO.INICIAL //consumimos recursos iniciales
		        simularTiempo()
		        tareasHechas+2
		        this.estado=ESTADO.EJECUCIÓN
		        listaIdTarea.removeFirst()
		        listaIdTarea.removeFirst()
		        this.iDTareaActual=listaIdTarea.getFirst() //actualizamos idActual
		    else if monitor.tieneRecurso(iDTareaActual) //consultamos si la tarea actual tiene recurso asignado consultado el id
		        simularTiempo()
		        this.tareasHechas++;
		        this.recursosAsignados++;
		        listaIdTarea.remove(listaIdTarea.indexOf(iDTareaActual))
		        this.iDTareaActual=listaIdTarea.getFirst() //actualizamos idActual
		     else 	//en caso de no tener recurso asignado la tarea incrementamos fallos
		        this.numFallos++
		        exmListaPeticionesAsignación.wait()
		        monitor.listaPeticionesAsignacion.add(this) //Volvemos a realizar la petición
		        exmListaPeticionesAsignación.signal()
		        sincronizaciónEjecución.wait() //esperamos el signal desde el gestor tras la asignación de 1 recurso
		        if monitor.tieneRecurso(listaIdTarea.get(i))
		            simularTiempo()
		            this.tareasHechas++
		            this.recursosAsignados++
		            listaIdTarea.remove(listaIdTarea.indexOf(tareaActual))
		            this.iDTareaActual=listaIdTarea.getFirst() //actualizamos idActual
		        else
		            //sustituimos la que lleva mas tiempo ejecutando
		            listaIdTarea.removeFirst()
		            this.tareasHechas++
					this.recursosAsignados--
		            this.iDTareaActual=listaIdTarea.getFirst() //actualizamos idActual
		        fin if
		     fin if
		fin while
		exmLiberacion.wait()
		monitor.peticionesLiberacion.add(this.id) //añadimos este proceso a la lista de los que han solicitado la liberación de recursos
		exmLiberacion.signal()
	   Fin func
	```
#### Gestor (Monitor monitor)
- Variables locales:
    - `id: Entero` Identificador. 
- Funciones:
    ```
	 func ejecucion
		while(!interrumpido)
		    exmListaPeticionesLiberación.wait()
		    monitor.atenderSolicitudesLiberacion() //liberamos
		    exmListaPeticionesLiberación.signal()
		    exmListaPeticionesAsignación.wait() //acceso seguro al la lista de peticiones
		    Proceso peticion=listaPeticiones.removeFirst()
		    int recursosAsignados=monitor.asignarRecursos(peticion)
		    if recursosAsignados==2
		        sincronizacionInicial.signal()  //pasamos a la fase de ejecucion
		    else 
				if recursosAsignados==1
		          monitor.recursos.add(peticion.getTareaActual())
				fin if
				sicronizaciónEjecución.signal() //comunicamos la asignación de 0 o 1 recursos
		    fin if
		    exmListaPeticionesAsignación.signal()       
		fin while
	   Fin func
	```
#### Monitor
- Variables:
    - `peticionesLiberacion: Lista<Proceso>` Lista donde se guardan los procesos que han pedido liberación de recursos.
    - `peticionesAsignacion: Lista<Proceso>` Lista donde se guardan los procesos que han pedido una asignación de recursos.
    - `recursos: Lista<Entero>` Lista donde guardamos los id de la tarea a la que ha sido asignada cada recurso. De tamaño `MAX_NUM_RECURSOS_ASIGNAR`.
- Funciones:
     ```
	 func asignarRecursos(Proceso proceso) -> int
		if proceso.getEstado() == inicial && proceso.getNumRecursosAsignados+2<=MAX_NUM_RECURSOS_ASIGNADOS && this.recursos.size-2>0
		        return 2
		else
		    if proceso.getNumRecursosAsignados+1<=MAX_NUM_RECURSOS_ASIGNADOS && this.recursos.size-1>0
	            return 1
	        else 
	            return 0 //no es posible asignar
	        fin if
	     fin if
	 Fin func
	```

	 ```
	 func atenterSolicitudesLiberacion() 
		for i until peticionesLiberacion.size()
		    for i until procesos.size()
		        if procesos.get(i)==peticionesLiberacion.get(i).getId()
		            procesos.remove(i)
		            i-- //como borramos una posicion retrasamos la i
		        fin if
		    fin for
		fin for
	 Fin func
	```
	 ```
	 func tieneRecurso(int idTarea) -> bool
		for i to recursos.size()
		    if recursos.get(i)==idTarea
		        return true
		    fin if
		        return false
		fin for
	 Fin func
	```
#### Hilo principal
- Hilo principal:
     ``` 
    func hilo principal
	    exmListaPeticionesLiberación(1)
	    exmListaPeticionesAsignación(1)
        sincronizaciónInicial(0)
        sincronizaciónEjecucion(0)
        crearGestor()
        creaMonitor()
 	    crearProcesos()
 	    ejecución()
        finalización()
        mostrarCadaProceso()
            Tiempo de inicio
            Tiempo de ejecucion
            getNumFallos()
        fin mostrarCadaProceso()
        calcularEstadisticas()
          Num procesos sin concluir
          Media de recursos
          Media de fallos
          fin calcularEstadisticas()
	 Fin func
	```
