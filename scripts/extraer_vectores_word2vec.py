import os
import re
import struct
import zipfile

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ZIP = os.path.join(RAIZ, "modeloIA", "archive.zip")
NOMBRE = "GoogleNews-vectors-negative300.bin"
RECURSOS = os.path.join(RAIZ, "src", "main", "resources")
ARTICULOS = os.path.join(RECURSOS, "articulos.txt")
SALIDA = os.path.join(RECURSOS, "vectores", "word2vec.txt")
FALTANTES = os.path.join(RECURSOS, "vectores", "word2vec_faltantes.txt")


def palabras_del_corpus():
    vocabulario = set()
    for linea in open(ARTICULOS, encoding="utf-8"):
        campos = linea.rstrip("\n").split("\t")
        if len(campos) >= 3:
            vocabulario.update(re.findall(r"[a-z]+", campos[2].lower()))
    return vocabulario


def leer_vectores(vocabulario):
    encontrados = {}
    with zipfile.ZipFile(ZIP) as z, z.open(NOMBRE) as f:
        encabezado = f.readline().split()
        total = int(encabezado[0])
        dimension = int(encabezado[1])
        tamano = 4 * dimension
        buffer = bytearray()
        posicion = 0
        for numero in range(total):
            while True:
                fin = buffer.find(b" ", posicion)
                if fin != -1 and fin + 1 + tamano <= len(buffer):
                    break
                trozo = f.read(1 << 25)
                if not trozo:
                    return encontrados, dimension, numero
                buffer = buffer[posicion:] + trozo
                posicion = 0
            palabra = buffer[posicion:fin].decode("utf-8", "ignore").lstrip("\n")
            if palabra in vocabulario:
                encontrados[palabra] = struct.unpack("<%df" % dimension, buffer[fin + 1:fin + 1 + tamano])
            posicion = fin + 1 + tamano
            if numero % 500000 == 0:
                print("leidas", numero, "de", total, "palabras;", len(encontrados), "encontradas", flush=True)
    return encontrados, dimension, total


vocabulario = palabras_del_corpus()
print("palabras distintas del corpus:", len(vocabulario), flush=True)
encontrados, dimension, leidas = leer_vectores(vocabulario)
faltantes = sorted(vocabulario - set(encontrados))
print("modelo:", leidas, "palabras de", dimension, "dimensiones")
print("encontradas:", len(encontrados), "faltantes:", len(faltantes))

os.makedirs(os.path.dirname(SALIDA), exist_ok=True)
with open(SALIDA, "w", encoding="utf-8") as salida:
    for palabra in sorted(encontrados):
        salida.write(palabra + " " + " ".join("%.6g" % x for x in encontrados[palabra]) + "\n")
with open(FALTANTES, "w", encoding="utf-8") as salida:
    salida.write("\n".join(faltantes) + "\n")
print("listo")
