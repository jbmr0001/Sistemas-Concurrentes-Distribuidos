[![logo](https://www.gnu.org/graphics/gplv3-127x51.png)](https://choosealicense.com/licenses/gpl-3.0/)
## Segunda Práctica

# Problema a resolver:
Para la solución de la práctica se utilizará como herramienta de concurrencia el desarrollo de monitores. Hay que tener presenta a la hora de implementar la solución que Java no dispone de esta herramienta. Para ello el alumno podrá utilizar cualquier utilidad de concurrencia para diseñar la clase que representa al monitor de la solución teórica. En la implementación del monitor se deberán respetar las características que presenta el monitor que se ha presentado en teoría.


Para un restaurante tenemos dos tipos de clientes: clientes estándar y premium. Como los clientes premium tienen una tarjetan de fidelización, por la que pagan una cuota y solicitan platos más caros, el restaurante ha decidido dar prioridad a los clientes premium.


- Los clientes tienen priorizada la entrada al restaurante. Los clientes pueden llegar en un mismo momento al restaurante (cada cliente tarda en llegar un tiempo entre 0 y 10 segundos). Simulamos la generación de clientes de forma aleatoria y uniforme  (la mitad premium y la otra mitad estándar) y un espacio del restaurante para 10 personas. Entran mientras haya huecos, pero cuando se llena deben esperar en una cola. Cuando un cliente sale entra otro, pero siempre que haya un hueco libre entra un cliente premium primero, aunque el estándar lleve más rato esperando. Habrá que establecer algún criterio para que los clientes estándar no esperen de forma indefinida.


- Cuando los clientes están en el restaurante piden varios platos de diferente precio. Los clientes piden entre 3-5 platos. Los premium piden de un precio entre 10-40 € y los clientes estándar entre 5-20 €. Todos se sientan en mesas individuales y también piden y pagan individualmente.


- La cocina atiende los platos, no en orden de pedida, sino preparando el de precio más alto para que los clientes premium esperen menos. El tiempo que se tarda en preparar un plato estará simulado y será de un segundo.


- Cuando un plato de un cliente ha sido preparado, el cliente se lo puede comer. Para simplificar supondremos que el camarelo lo ha servido en su mesa. El cliente tarda entre 2-5 segundos en comerlo.


- Cuando acaba con el último plato, el cliente se marcha.


- Para la solución hay que tener presente:

    - Diseñar el monitor que representa al restaurante.
    - Diseñar los procesos necesarios, incluido el proceso principal.
    - Definir todas las constantes necesarias.

Para la implementación se tendrá que mostrar la siguiente información:

 - El momento que un cliente llega al restaurante.
 - El momento en el que el cliente entra en el restaurante.
 - Los platos que ha pedido el cliente y el precio de los mismos.
- El momento en que abandona el restaurante.
 - Recaudación del restaurante.

# Análisis y Diseño:
Estructuras de datos, variables compartidas y procedimientos necesarios.
### Constantes
- `MAX_TIEMPO_LLEGAR_CLIENTE(10): Entero` Entero con el tiempo máximo que tarda un cliente en llegar al restaurante.
- `MIN_TIEMPO_LLEGAR_CLIENTE(0): Entero` Entero con el tiempo mínimo que tarda un cliente en llegar al restaurante.
- `MAX_CLIENTES_RESTAURANTE(10): Entero` Entero con el número de personas máximo que pueden estar en el restaurante. 
- `NUM_CLIENTES(20): Entero` Entero con el número total de clientes que llegar durante la ejecución.
- `MAX_NUM_PLATOS(3): Entero` Entero con el número máximo de platos que puede pedir un cliente.
- `MIN_NUM_PLATOS(5): Entero` Entero con el número mínimo de platos que puede pedir un cliente.
- `MAX_PRECIO_PREMIUM_PLATO(40): Entero` Entero con el precio máximo que puede tener un plato de tipo PREMIUM.
- `MIN_PRECIO_PREMIUM_PLATO(10): Entero` Entero con el precio mínimo que puede tener un plato de tipo PREMIUM.
- `MAX_PRECIO_ESTANDAR_PLATO(20): Entero` Entero con el precio máximo que puede tener un plato de tipo ESTANDAR.
- `MIN_PRECIO_ESTANDAR_PLATO(5): Entero` Entero con el precio mínimo que puede tener un plato de tipo ESTANDAR.
- `MAX_TIEMPO_CLIENTE_COMIENDO(5): Entero` Entero con el tiempo máximo que puede estar un cliente comiendo un plato.
- `MIN_TIEMPO_CLIENTE_COMIENDO(2): Entero` Entero con el tiempo máximo que puede estar un cliente comiendo un plato.
- `TIEMPO_COCINA(1): Entero` Entero con el tiempo que tarda la concina en preparar un plato. En la implementación lo he puesto a 3 para que tarden los clientes más en salir del restaurante y probar el bloqueo cuando el restaurante está lleno.
- `Tipo: Enum` Enum para indicar el tipo de cliente. Puede ser ESTANDAR o PREMIUM.
- `MAX_RACHA_PREMIUM(4): Entero` Entero para indicar el número de clientes premium que han accedido seguidos. Por cada acceso de MAX_RACHA_PREMIUM clientes premium accede uno ESTANDAR de la cola. Mecanismo de acceso.
- `ESPERA_CLIENTES(120): Entero` Entero con el tiempo a esperar antes de cancelar el proceso concina.
### Variables compartidas
- El propio monitor es el único elemento compartido.

### Tipos de datos

#### MonitorRestaurante
- Variables locales:
    - `platosPedidos: Lista<Plato>` Lista con los platos pedidos a la cocina.
    - `capadidadDisponible: Entero` Entero con el número de clientes que pueden entrar en el restaurante.
    - `colaEntrada: Lista<Condicion>` Lista de variables de condición para bloquear clientes cuando la capacidad del restaurante esté al máximo. En la posición 0 almacenaremos los de tipo ESTANDAR y el la posición 1 los de tipo PREMIUM.
    - `bloqueoCliente: Lista<Condicion>` Lista de variables de condición para bloquear cada proceso a la espera de ser servido.
    - `idPlatoActual: Entero` Entero con el identificador para guardar el último plato pedido y así asignar a cada plato pedido un id. 
    - `platosPreparadosServir: Lista<Entero>` Lista de enteros para almacenar los platos que han sido preparados a cada cliente.
    - `premiumEsperando: Entero` Entero para almacenar el número de clientes PREMIUM que hay esperando. De esta manera controlamos la prioridad de acceso.
    - `premiumSeguidos: Entero` Entero para indicar la racha de clientes premium en el acceso.
    - `totalGanado: Entero` Entero para contabilizar la recaudación total del restaurante.
    - `clientesRecibidos: Entero` Entero para contabilizar en número total de clientes recibidos. 
    
- Funciones:
    ```
    /**Función con la que cada cliente pide su número de platos**/
	 func pedir(Entero cliente,Tipo tipo, Entero numPlatos) 
	    for i = 0 UNTIL numPlatos //Insertamos numPlatos platos en la lista
	        int precio
	        if tipo == ESTANDAR //Asignamos precio según el tipo
	            precio = aleatorioEnRango(MIN_PRECIO_ESTANDAR_PLATO,MAX_PRECIO_ESTANDAR_PLATO)
	        else
	            precio = aleatorioEnRango(MIN_PRECIO_PREMIUM_PLATO,MAX_PRECIO_PREMIUM_PLATO)
	        fin if
	        Plato plato = nuevoPlato(idPlatoActual,precio,cliente)
	        idPlatoActual++ //Actualizamos el id del último plato asignado
	        platosPedidos.add(plato)
	    fin for
	 Fin func
	```
	```
	/**Función para obtener el siguiente plato a preparar por parte de la cocina. Buscamos el plato con mayor precio de la lista de pedidos y lo devolvemos Devuelte un elemento de clase Plato con el plato**/
	 func siguientePlato() -> Plato
	    Plato plato = nuevoPlato(0,0,0) //Generamos un plato default para casos en los que no haya platos pendiendes
	    if platosPedidos.size() > 0 //En caso de haber platos pendientes
	       int mayorPrecio = 0
	       int indiceMayor = 0
	       for i = 0 UNTIL platosPedidos.size() //Buscamos el plato de mayor precio
	            if platosPedidos.get(i).getPrecio() > mayorPrecio //Estructura para encontrar el mayor
	                //Actualizamos variables con el nuevo mayor
	                mayorPrecio = platosPedidos.get(i).getPrecio()
	                plato = platosPedidos.get(i)
	                indiceMayor = i
	            fin if
	       fin for
	       plato = platosPedidos.remove(indiceMayor) //Eliminamos al mayor de la lista de platos pedidos y lo devolvemos
	    fin if
	    return plato
	 Fin func
	```
	```
	 /**Función para preparar los platos para servir a cliente**/
	 func prepararServir(Plato plato) 
	    //Incrementamos en la lista de platos preparados para servir en 1 en la posicion del cliente
	    platosPreparadosServir.set(plato.getCliente(),platosPreparadosServir.get(plato.getCliente()++)
	    bloqueoCliente.get(plato.getCliente()).resume() //Desbloqueamos al cliente a la espera de platos
	    totalGanado+=plato.getPrecio() //Contabilizamos el precio del plato
	 Fin func
	```
	```
	/**Función para bloquear al cliente a la espera de ser servido**/
	 func esperarPlato(Entero cliente) 
	    bloqueoCliente.get(cliente).delay() //Bloqueamos el cliente hasta que nos despierte la cocina para servirnos
	 Fin func
	```
	```
	/**Función para controlar la entrada de clientes al restaurante metiendolos en su correspondiente cola si no puede entrar**/
	 func solicitarEntrada(Tipo tipo) 
	    if capadidadDisponible > 0 //Si hay hueco entramos
	        capadidadDisponible--
	    else //Si no hay hueco nos bloqueamos en la variable de condición de nuestro tipo a la espera de que nos dejen entrar
	        if tipo == PREMIUM //Contabilizamos los premium que hay esperando para darles prioridad
	            premiumEsperando++
	        fin if
	        colaEntrada.get(tipo).delay() 
	        capacidadDisponible--
	       
	    fin if
	 Fin func
	```
	```
	/**Función para dejar entrar a otro cliente cuando un cliente sale del restaurante**/
	 func salir() 
	    
	        //Si hay PREIMUM esperando o llevamos menos de MAX_RACHA_PREMIUM PREMIUM seguidos (Mecanismo para hacer que los ESTANDAR no esperen indefinidamente) desbloqueamos un cliente PREMIUM
	        if premiumEsperando != 0 && premiumSeguidos < MAX_RACHA_PREMIUM
	            colaEntrada.get(PREMIUM).resume() //Desbloqueamos un PREMIUM y actualizamos variables
	            premiumSeguidos++
	            premiumEsperando--
	        else
	            colaEntrada.get(ESTANDAR).resume() //Desbloqueamos un ESTANDAR y actualizamos variables
	            premiumSeguidos = 0
	        fin if
	        clientesRecibidos++ 
	        capacidadDisponible++ //Dejamos hueco
	    
	 Fin func
	```
	```
	/**Función para hacer un get de los platos preparados a un cliente**/
	 func verPlatosPreparadosServir(Entero cliente) -> Entero 
	    return platosPreparadosServir.get(cliente) //Devolvemos su posición en la lista
	 Fin func
	```
	```
	/**Función para hacer un print de lo total facturado por el restaurante**/
	 func mostrarTotalRecaudado() 
	    mostrar(totalGanado)
	 Fin func
	```
#### Cocina
- Variables locales:
    - `monitor: MonitorRestaurante` Clase monitor usada para la resolución de nuestro problema.
	
- Funciones:
    ```
	 func ejecucion
		while NO interrumpido
		    Plato plato = monitor.siguientePlato()
		    //Si el precio de un plato es 0 es porque se trata del plato default por lo que no habrá platos para servir
		    if plato.getPrecio() == 0 //Cuando no haya platos disponibles no servimos
		        print(No hay pedidos)
		    else
		        monitor.prepararServir(plato)
		        simularTiempo(TIEMPO_COCINA)
		    fin if
		fin while
	   Fin func
	```
	
#### Cliente
- Variables locales:
    - `id: Entero` Identificador por si hubiera mas cocinas aunque para este problema solo tengamos una cocina.
    - `tipo: Tipo` Enum para indicar el tipo de cliente. PREMIUM O ESTANDAR.
    - `monitor: MonitorRestaurante` Clase monitor usada para la resolución de nuestro problema.
    - `pedidos: Entero` Entero para almacenar el número de platos que ha pedido el cliente.
	
- Funciones:
    ```
	 func tipo() //Funcion para asignar el tipo a los clientes de forma uniforme
		if id % 2 == 0 //Si es par es PREMIUM
		    tipo = PREMIUM
		else
		    tipo = ESTANDAR
		fin if
	 Fin func
	```
	```
	 func ejecucion
		monitor.solicitarEntrada(tipo) //Esperamos que nos dejen entrar
		int numPlatos = aleatorioEnRango(MIN_NUM_PLATOS,MAX_NUM_PLATOS)
		monitor.pedir(id,tipo,numPlatos)//Pedimos nuestros platos
		for i = 0 UNTIL numPlatos
		    monitor.esperarPlato() //Esperamos que nos sirvan
		    while i <= monitor.verplatosPreparadosServir(id) 
		    //Puede ser que cuando la cocina nos haga un resume haya mas de un plato.
		    //Con este bucle simulamos la entrega uno a uno al cliente y el consumo de este.
		        int tiempoComiendo = aleatorioEnRango(MIN_TIEMPO_CLIENTE_COMIENDO,MAX_TIEMPO_CLIENTE_COMIENDO)
		        simular(tiempoComiendo)
		        i++
		    fin while
		    monitor.salir() //Dejamos hueco y despertamos otro cliente
		fin for
	 Fin func
	```
#### Plato
- Variables locales:
    - `id: Entero` Identificador para diferenciar un plato de otro.
    - `precio: Entero` Entero para guardar el precio del plato.
    - `cliente: Entero` Entero para guardar el cliente que pidió el plato.
     
#### Hilo principal
- Hilo principal:
     ``` 
    func hilo principal
	    crearMonitor()
	    crearCocina()
	    ejecutarCocina() //Lanzamos la cocina
	    int clientes = 0
	    while clientes < NUM_CLIENTES //Lanzamos los clientes
	        crearCliente()
	        ejecutarCliente()
	        int tardaLlegar = aleatorioEnRango(MIN_TIEMPO_LLEGAR_CLIENTE,MAX_TIEMPO_LLEGAR_CLIENTE)
	        simularTiempo(tardaLlegar)
	    fin while
	    simularTiempo(ESPERA_CLIENTES) //Esperamos a que todos los clientes terminen antes de cerrar la cocina
	    cancelarCocina()
	    monitor.mostrarTotalRecaudado()
	 Fin func
	```