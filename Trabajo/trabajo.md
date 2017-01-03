# Lógica difusa aplicada al diagnóstico médico

> Francisco Javier Bolívar Lupiáñez

## Introducción

En la medicina los médicos usan a veces variables precisas como pueden ser el peso, la altura, la frecuencia cardiaca en reposo, pero en la mayoría de las ocasiones usa otras con más vaguedad como puede ser el grado de dolor de cabeza que sufre el paciente, el ejercicio que realiza diariamente e incluso algunas de las variables precisas mencionadas anteriormente como la frecuencia cardiaca.

Es por esto que la aplicación de la lógica difusa en este campo ha tenido bastante importancia que se ve reflejada en el número de papers que se publican sobre esto [1]:

| Año  | Publicaciones |
| ---- | ------------- |
| 2000 | 96            |
| 2001 | 151           |
| 2002 | 119           |
| 2003 | 141           |
| 2004 | 182           |
| 2005 | 194           |
| 2006 | 277           |
| 2007 | 253           |
| 2008 | 290           |
| 2009 | 312           |
| 2010 | 306           |

## Proceso

El proceso de un sistema difuso cuenta con los siguiente pasos [2]:

!["Pasos"](img/fuzzy-process-steps.png)

## Un caso práctico. Diagnóstico de riesgo de enfermedad cardíaca

[3,4]

Se usa el *toolbox* de lógica difusa de MatLab para desarrollar el sistema.

### Fuzzificar

#### Entradas

##### Presión arterial sistólica

Valor máximo de presión arterial cuando el corazón se contrae (sístole). Se mide en milímetros de mercurio (mmHg).

La dividimos en cuatro valores difusos:

IMAGEN

ECUACIÓN

##### Colesterol LDL

Una mayor cantidad de colesterol LDL (*low-density lipoprotein*) se traduce en mayor riesgo de sufrir un ataque al corazón. Se mide en miligramos por decilitro de sangre (mg/dl).

Lo dividimos en cuatro valores difusos:

IMAGEN

ECUACIÓN

##### Electrocardiograma (ECG)

El ECG es la representación de la actividad eléctrica del corazón. Se obtiene con ultrasonido usando un electrocardiógrafo.

IMAGEN

En nuestro caso se mide en segundos la duración del segmento ST y la onda T.

Lo dividimos en tres valores difusos:

IMAGEN

ECUACIÓN

##### Frecuencia cardíaca

La frecuencia cardíaca es cuántas veces late el corazón por minuto. Se ha de medir en reposo.

Lo dividimos en tres valores difusos:

IMAGEN

ECUACIÓN

##### Glucosa en sangre

Una persona con más de 120 mg/dl de glucosa en sangre es diabética lo que aumenta el riesgo de que le produzca un ataque al corazón.

Vamos a usar un único valor difuso:

IMAGEN

ECUACIÓN

##### Depresión del segmento ST

Medir la depresión del segmento ST durante el ejercicio puede ayudar a pronosticar una enfermedad del corazón. A mayor sea este, más posibilidades de sufrir un ataque al corazón.

Se usarán tres variables difusas:

IMAGEN

ECUACIÓN

##### Escáner con talio

Con el escáner con talio se puede ver si zonas del corazón no reciben suficiente sangre.

Se utilizarán tres valores difusos:

IMAGEN

ECUACIÓN

##### Edad

A más años tenga un individuo, mayor es la probabilidad de que sufra un ataque cardíaco.

Se usarán cuatro variables difusas:

IMAGEN

#### Salida

El sistema proveerá una única salida con el porcentaje de riesgo de ataque.

Para ello se usarán cinco variables difusas:

IMAGEN

## Referencias

1. Patel, A., Gupta, S. K., Rehman, Q., & Verma, M. K. (2013). Application of Fuzzy Logic in Biomedical Informatics. *Journal of Emerging Trends in Computing and Information Sciences, 4*(1), 57-62.
2. Tarhini, A., Kočiš, L'. (1998). Fuzzy Approach in Psychiatry. *Psychiatria*, 80-86.
3. Sikchi, S. S., Sikchi, S., & Ali, M. S. (2013). Generic medical fuzzy expert system for diagnosis of cardiac diseases. *International Journal of Computer Applications, 66*(13).
4. Adeli, A., & Neshat, M. (2010, March). A fuzzy expert system for heart disease diagnosis. In *Proceedings of International Multi Conference of Engineers and Computer Scientists, Hong Kong* (Vol. 1).
