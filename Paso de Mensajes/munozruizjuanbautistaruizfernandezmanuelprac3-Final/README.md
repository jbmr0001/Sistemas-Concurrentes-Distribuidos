[![logo](https://www.gnu.org/graphics/gplv3-127x51.png)](https://choosealicense.com/licenses/gpl-3.0/)
# Tercera Práctica
## Resolución con paso de mensajes

Para la resolución de análisis y diseño se deberán utilizar paso de mensajes asíncronos como herramienta para la programación concurrente.

Para la implementación de la práctica se utilizará como herramienta de concurrencia JMS (Java Message Service). Esta práctica es una práctica en grupo de hasta dos alumnos y cada grupo deberá crear en el _broker_ sus propios _destinos_ para sus mensajes. Cada destino deberá definirse siguiendo la siguiente estructura:

```
....
// En la interface Constantes del proyecto 
public static final String DESTINO =
 "ssccdd.curso2021.NOMBRE_GRUPO.BUZON";
...
```

El nombre del grupo tiene que ser único para los grupos, por lo que se recomienda usar alguna combinación de los nombres de los integrantes del grupo.

## Problema a resolver
Para un restaurante tenemos dos tipos de clientes: clientes **estándar** y **premium**. Como los clientes premium tienen una tarjetan de fidelización, por la que pagan una cuota y solicitan platos más caros, el restaurante ha decidido dar prioridad a los clientes premium.

- Los clientes tienen priorizada la entrada al restaurante. Los clientes pueden llegar en un mismo momento al restaurante (cada cliente tarda en llegar un tiempo entre 0 y 10 segundos). Simulamos la generación de clientes de forma aleatoria y uniforme  (la mitad premium y la otra mitad estándar) y un espacio del restaurante para 10 personas. Entran mientras haya huecos, pero cuando se llena deben esperar en una cola. Cuando un cliente sale entra otro, pero siempre que haya un hueco libre entra un cliente premium primero, aunque el estándar lleve más rato esperando. Habrá que establecer algún criterio para que los clientes estándar no esperen de forma indefinida.

- Cuando los clientes están en el restaurante piden varios platos de diferente precio. Los clientes piden entre 3-5 platos. Los premium piden de un precio entre 10-40 € y los clientes estándar entre 5-20 €. Todos se sientan en mesas individuales y también piden y pagan individualmente.

- La cocina atiende los platos, no en orden de pedida, sino preparando el de precio más alto para que los clientes premium esperen menos. El tiempo que se tarda en preparar un plato estará simulado y será de un segundo.

- Cuando un plato de un cliente ha sido preparado, el cliente se lo puede comer. Para simplificar supondremos que el camarelo lo ha servido en su mesa. El cliente tarda entre 2-5 segundos en comerlo.

- Cuando acaba con el último plato, el cliente se marcha.

Para la solución hay que tener presente:

- Definir todas las constantes necesarias.

Para la implementación se tendrá que mostrar la siguiente información:

- El momento que un cliente llega al restaurante.
- El momento en el que el cliente entra en el restaurante.
- Los platos que ha pedido el cliente y el precio de los mismos.
- El momento en que abandona el restaurante.
- Recaudación del restaurante.

### Restricciones

Para la práctica cada integrante del grupo deberá repartirse el trabajo y para ello deberán definirse los siguientes procesos:

- ProcesoCliente, ProcesoPrincipal y los elementos comunes para el desarrollo del proyecto. En proceso cliente debe simular las acciones para el cliente con el restaurante. El proceso principal será el encargado para ir creando procesos cliente y obtener presentar la información que se pide a la finalización del restaurante.

- ProcesoCocinero y ProcesoRestaurante. El proceso cocinero será el encargado de preparar los diferentes platos que pide el cliente al restaurante. El proceso restaurante deberá centralizar las acciones que se llevarán acabo dentro del restaurante para atender a los clientes.

# Análisis y Diseño:

Cada proceso Cliente enviará una petición de entrada al Restaurante. Una vez tenga permiso para entrar, el cliente pedirá sus platos y quedará a la espera de que le sirvan todos. Estas peticiones de platos serán recibidas por el Restaurante y almacenadas en una cola. La petición de mayor precio será enviada a la cocina. Una vez la cocina prepare un plato lo enviará al Restaurante y este lo entregará su Cliente. Cuando el Cliente esté servido completamente se lo comunicará al Restaurante y este liberará una plaza. 
Una vez termine el restaurante mostrará todos los clientes atendidos, la cocina hará lo mismo con la sumatoria del precio de los platos.

Para la implementación hemos usado jdk 8. Para los mensajes usaremos diferentes clases que heredan de una clase principal Petición(String tipo,int id):
- `PeticionEntrada(...,tipoCliente)`
- `PeticionSalida(...,LocalDateTime llegada,LocalDateTime entrada,LocalDateTime salida,LinkedList<Plato> platosRecibidos)`
- `PeticionPlato(...,Plato p)`

Estructuras de datos, variables compartidas y procedimientos necesarios:

### Constantes

- `NUM_CLIENTES(20): Entero` Entero con el número de Clientes a generar.
- `CAPACIDAD_RESTAURANTE(10): Entero` Entero con la capacidad máxima del Restaurante.
- `MIN_TIEMPO_LLEGAR_CLIENTE(0): Entero` Entero con el tiempo mínimo en segundos que tarda en llegar un Cliente
- `MAX_TIEMPO_LLEGAR_CLIENTE(10): Entero` Entero con el tiempo máximo en segundos que tarad en llegar un Cliente.
- `MAX_PREMIUM_SEGUIDOS(4): Entero` Entero con el máximo número de Clientes PREMIUM que pueden acceder seguidos, mecanismo para controlar.
- `MAX_NUM_PLATOS(5): Entero` Entero con el número máximo de Platos que puede pedir un Cliente.
- `MIN_NUM_PLATOS(3): Entero` Entero con el número mínimo de Platos que puede pedir un Cliente.
- `MAX_PRECIO_PLATO_ESTANDAR(20): Entero` Entero con el precio máximo de un Plato ESTANDAR.
- `MIN_PRECIO_PLATO_ESTANDAR(5): Entero` Entero con el precio mínimo de un Plato ESTANDAR.- `MAX_PRECIO_PLATO_PREMIUM(40): Entero` Entero con el precio máximo de un Plato PREMIUM.
- `MIN_PRECIO_PLATO_PREMIUM(10): Entero` Entero con el precio mínimo de un Plato PREMIUM. 
- `MIN_TIEMPO_COMIENDO(2): Entero` Entero con el tiempo en segundos mínimo que un Cliente tarda en comer un Plato. 
- `MAX_TIEMPO_COMIENDO(5): Entero` Entero con el tiempo en segundos máximo que un Cliente tarda en comer un Plato.
- `TIEMPO_ESPERA_RESTAURANTE(120): Entero` Entero con el tiempo en segundos que el Restaurante esperará para finalizar.
- `TIEMPO_COCINA_PLATO (1): Entero` Entero con el tiempo que se tarda en cocinar un plato

### Buzones
- `buzonRestaurante: Buzon` Buzón del Restaurante para recibir peticiones de entrada, salida, platos pedidos y platos preparados.
- `buzonCocina: Buzon` Buzón de la Cocina para recibir peticiones de platos desde el Restaurante.
- `respuestaEntrada[id]: Buzon` Buzón del Cliente para recibir la respuesta del Restaurante a la petición de entrada.
- `buzonPlatos[id]: Buzon` Buzón del Cliente para recibir los platos desde el Restaurante una vez la Cocina los ha preparado.

### Tipos de datos

#### Cliente

##### Variables locales y buzones:

- `id: Entero` Entero único para identificar un Cliente de otro.
- `tipo: Tipo` Enum para indicar el tipo de Cliente. Pude ser ESTANDAR O PREMIUM.
- `numPlatos: Entero` Entero para indicar el número de platos que va a pedir el Cliente.
- `llegada: LocalDateTime` LocalDateTime para indica r cuándo llega el Cliente a la puerta del restaurante.
- `entrada: LocalDateTime` LocalDateTime para indicar cuándo entra el Cliente al restaurante.
- `salida: LocalDateTime` LocalDateTime para indicar cuándo sale el Cliente del restaurante.
- `platosRecibidos: LinkedList<Plato>` Lista de platos que ha recibido el Cliente. 
- `idPlatos: Atomic<Entero>` Entero atómico para crear ids únicos de los platos.
- `buzonRestaurante: Buzón` Buzón del restaurante al que le enviaremos las peticiones de entrada y las peticiones de platos.
- `respuestaEntrada[id]: Buzón` Buzón para recibir la confirmación de entrada al Restaurante.
- `buzonPlatos[id]: Buzón` Buzón para recibir los platos desde el restaurante.

##### Funciones:

```
/**Función de ejecución de cada proceso Cliente**/

func ejecucion()
	send(buzonRestaurante,PeticionRestaurante("Entrada",this.id,this.tipo)
	recive(respuestaEntra,"Permiso")//Bloqueamos hasta recibir permiso para entrar.
	
	for i=0 to numPlatos
		//Calculamos el precio según el tipo de cliente
		if this.tipo == ESTANDAR
			precio=aleatorioEnRango(MIN_PRECIO_PLATO_ESTANDAR,MAX_PRECIO_PLATO_ESTANDAR)
		else
			precio=aleatorioEnRango(MIN_PRECIO_PLATO_PREMIUM,MAX_PRECIO_PLATO_PREMIUM)
		fin if
		//Pedimos el plato.
		Plato plato(idPlatos.getAndIncrement,precio,this.id)
		send(buzonRestaurante,PeticionPlato("Plato",this.id,plato
	fin for
	//Recibimos los platos con un listener hasta recibir todos los pedidos.
	while(platosRecibidos.size!=numPlatos)
		    Select 
				recive(buzonPlatos[id],plato)
				recibePlato(plato)
	fin while
	
	send(buzonRestaurante,PeticionSalida("Salida",this.id,this.llegada,this.entrada,this.salida,this.platosRecibidos)
	
Fin func
```

```
/**Función para añadir un plato una vez es recibido**/
	func recibirPlato(Plato plato)
		platosRecibidos.add(plato)
		simularTiempo(MIN_TIEMPO_COMIENDO,MAX_TIEMPO_COMIENDO)
	fin func
```

#### Cocina

##### Variables locales y buzones:

- `listaPlatos: LinkedList<Plato>` Lista de peticiones de platos.
- `sumaPlatos: Entero` Entero en el que vamos sumando el precio de los platos preparados.
- `buzonCocina: Buzon` Buzón de la cocina al que le llegan las peticiones de platos de los clientes a través del restaurante.
- `buzonRestaurante: Buzon` Buzón del restaurante al que se le envian los platos ya preparados.

##### Funciones:

```
/** Función de ejecución del proceso cocina **/

func ejecucion()
	while(!interrumpido)
		select //Recibimos peticiones de platos y las metemos en la cola.
			receive(peticionesPlato, p)
			listaDePlatos.add(p)
		OR //Si hay platos en la cola los preparamos.
			when(listaDePlatos.size!=0)
			Plato pla=listaDePlatos.removeFirst();
			sumaPlatos= sumaPlatos + pla
			simularTiempo(1)
			send(buzonRestaurante,pla)			
	fin while
fin func
```

#### Restaurante

##### Variables locales y buzones:

- `capacidad: Entero` Entero único para identificar un Cliente de otro.
- `colaPremium: LinkedList<Entero>` Lista con los id de los clientes PREMIUM que han solicitado entrar.
- `colaEstandar: LinkedList<Entero>` Lista con los id de los clientes ESTANDAR que han solicitado entrar.
- `seguidosPremium: Entero` Entero con la racha de Clientes PREMIUM actual, para controlar que no entren siempre.
- `colaEnvio: LinkedList<Plato>` Lista con los platos recibidos desde la Cocina listos para ser enviados a su Cliente.
- `platosPedidos: LinkedList<Plato>` Lista con los platos pedidos por los clientes.
- `clientesServidos: Entero` Lista con los clientes que han acabado su ejecución, han sido servidos y han comido sus platos, para mostrar sus datos.
- `buzonRestaurante: Buzon` Buzon en el que el restaurante recibe las peticiones de platos, entrada, salida y platos realizados.
- `buzonCocina: Buzon` Buzon al que mandará el restaurante las peticiones de platos de los clientes.
- `respuestaEntrada[idCliente]: Buzón` Buzón al que enviará el restaurante las confirmaciones de entrada a los clientes.
- `buzonPlatos[idCliente]: Buzón` Buzón para enviar los platos desde el restaurante al cliente
##### Funciones:

```
/** Función de ejecución del proceso restaurante **/

func ejecucion()
	while(!interrumpido)
		select //Peticion de entrada.
			recive(buzonRestaurante,peticionEntrada)
			añadirClienteCola(peticionEntrada.getID(),peticionEntrada.getTipo()
		OR //Peticion de Plato.
			recive(buzonRestaurante,peticionPlato)
			añadirPlatoCola(peticionPlato.getPlato())
		OR //Plato preparado que metemos en la cola de envío.
			recive(buzonRestaurante,platoPreparado)
			añadirColaEnvio(platoPreparado.getPlato)
		OR //Petición de salida.
			recive(buzonRestaurante,peticionSalida)
			eliminarCliente(peticionSalida)
		OR //Si hay hueco dejamos pasar a un cliente.
			when capacidad!=0
				int cliente=siguienteCliente()
				if cliente !=-1
					capacidad--
				send(respuestaEntrada[cliente],"Confirmado")
				fin if
			fin when 
		OR //Si hay platos pedidos solcitamos a la Cocina que lo prepare.
			when platosPedidos.size()!=0
				int indice()
				Plato min()
				for i=0 to platosPedidos.size()
					if platosPedidos.get(i).getPrecio()>min.getPrecio()
					min=platosPedidos.get(i);
                    indice=i;
					fin if
				fin for
				send(buzonCocina,min)
			fin when
		OR //Si hay platos preparados, os enviamos.
			when colaEnvio.size()!=0
				Plato plato=colaEnvio.removeFirst()
				send(recibePlato[plato.getCliente()],Plato
			fin when
	fin while
	mostrarClientes()	
Fin func
```

```
/**Función para añadir los clientes que solicitan entrar, a su cola**/
	func añadirClienteCola(int id,String tipo)
		if tipo==ESTANDAR
			colaEstandar.add(id)
		else
			colaPremium.add(id)
		fin if
	fin func
```

```
/**Función para eliminar un cliente una vez sale del Restaurante **/
	func eliminarCliente(PeticionSalida p)
		clientesServidos.add(p)
		capacidad++
	fin func
```

```
/**Función **/
	func siguienteCliente() ->int
		if colaPremium.size()!=0 
			if seguidosPremium==MAX_PREMIUM_SEGUIDOS && colaEstandar.size()!=0
				seguidosPremium=0
				return colaEstandar.removeFirst() //Liberamos un estandar ya que llevamos 4 premium seguidos.
			fin if
			seguidosPremium++
			return colaPremium.removeFirst() //Si hay un premium y no pasamos del límite de premiums seguidos, devolvemos premium. 
		else if colaEstandar.size()!=0
				 return colaEstandar.removeFirst()
			 else
				 return -1 //Si no no ninguno en la cola.
			 fin if
	fin func
```
Primero ejecutamos la Cocina y el Restaurante(ProcesoPrincipal2) y luego el Cliente(ProcesoPrincipal1)
#### Proceso Principal 1
```
	for i = 0 to numClientes
		cliente = crearCliente
		ejecutaCliente(cliente)
		simularTiempo(MAX_TIEMPO_LLEGAR_CLIENTE)
	fin for
	esperamosFinalizaciónClientes()
```
#### Proceso Principal 2 
```
	 restaurante = crearRestaurante()
	 cocina=creaCocina()
	 ejecutaCocina(Cocina)
	 ejecutaRestaurante(restaurante)
	 esperarTiempo(150)
	 cancelarRestaurante(restaurante)
	 cancelarCocina(cocina)
```
