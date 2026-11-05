<p align="center">
  <img src="assets/icon.png" alt="StringsMatcher icon" width="160">
</p>

# StringsMatcher

Il progetto `StringsMatcher` è una libreria Java per confrontare due stringhe (in particolare **ragioni sociali**) e determinarne il livello di similarità tramite **quattro algoritmi differenti**. È pensato per aiutare nella deduplicazione, matching fuzzy e normalizzazione di nomi aziendali.

## ✨ Funzionalità principali

- Pulizia e normalizzazione delle stringhe (rimozione caratteri speciali, lowercase, trim)
- Calcolo della similarità tra stringhe tramite:
  - **Levenshtein Distance**
  - **Jaro-Winkler Similarity**
  - **Jaccard Distance**
  - **Sørensen-Dice Coefficient**
- Soglie configurabili per determinare il match
- Supporto per estrazione di caratteri speciali non alfanumerici

---

## 📦 Algoritmi implementati

### 1. Levenshtein Distance

Misura il **numero minimo di operazioni** (inserimenti, cancellazioni, sostituzioni) necessarie per trasformare una stringa nell'altra. La similarità è normalizzata tra 0 e 1.

- **Uso**: `getLevenshteinPoints(String s1, String s2)`
- **Match booleano**: `levenshtein(String s1, String s2)`
- **Soglia predefinita**: `0.6`

### 2. Jaro-Winkler Similarity

Algoritmo efficace su **nomi propri** e **stringhe brevi**, valuta:
- Matching dei caratteri
- Ordine relativo
- Prefisso comune

Aggiunge un bonus se le stringhe iniziano con lo stesso prefisso.

- **Uso**: `getJaroWinklerPoints(String s1, String s2)`
- **Match booleano**: `jaroWinkler(String s1, String s2)`
- **Soglia predefinita**: `0.90`

### 3. Jaccard Distance

Basato sull'**intersezione e unione di n-grammi** (sottostringhe). Utile per stringhe frammentate.

- **Uso**: `getJaccardPoints(String s1, String s2)`
- **Match booleano**: `jaccard(String s1, String s2)`
- **Soglia predefinita**: `0.40` (essendo una **distanza**, un valore più basso indica maggiore similarità)

### 4. Sørensen-Dice Coefficient

Simile a Jaccard ma **dà più peso all'intersezione**, risultando più permissivo.

- **Uso**: `getDicePoints(String s1, String s2)`
- **Match booleano**: `dice(String s1, String s2)`
- **Soglia predefinita**: `0.65`

---

## 🔧 Preprocessing

Le stringhe vengono **normalizzate automaticamente** prima del confronto tramite:

- `trim()` (rimozione spazi)
- `toLowerCase()`
- Rimozione dei seguenti caratteri:
  ```
  ".", "&", "-", "'", "!", ",", "(", ")", "*", "%", ":", "+"
  ```

Metodo usato: `normalizeString(String input)`

---

## 🔍 Altri metodi utili

### Estrazione caratteri speciali

```java
StringsMatcher.printUniqueNonLetterNonDigitCharacters(List<String> strings);
```

Stampa tutti i caratteri non alfanumerici unici presenti nella lista fornita.

---

## 🧪 Esempi d'uso

```java
boolean isSimilar = StringsMatcher.levenshtein("Acme S.p.A.", "Acme SpA");
// true se il punteggio >= 0.6

double score = StringsMatcher.getJaccardPoints("Gamma-Tech", "Gammatech");
// valore tra 0 e 1, più basso = più simili

boolean diceMatch = StringsMatcher.dice("Alfa Beta", "Alfa-Beta");
// true se punteggio >= 0.65
```

---

## 📂 Dipendenze

Richiede la libreria Apache Commons Lang:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```
