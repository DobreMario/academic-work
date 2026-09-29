module AlgebraicGraph where

import Data.Set (Set)
import qualified Data.Set as Set

data AlgebraicGraph a
    = Empty
    | Node { label :: a }
    | Overlay { left :: AlgebraicGraph a, right :: AlgebraicGraph a }
    | Connect { left :: AlgebraicGraph a, right :: AlgebraicGraph a }
    deriving (Ord)

-- (1, 2), (1, 3)
-- 1 * (2 + 3)
angle :: AlgebraicGraph Int
angle = Connect (Node 1) (Overlay (Node 2) (Node 3))

-- (1, 2), (1, 3), (2, 3)
-- 1 * (2 * 3)
triangle :: AlgebraicGraph Int
triangle = Connect (Node 1) (Connect (Node 2) (Node 3))

{-
*** TODO ***

Funcția nodes din etapa 2.
-}
nodes :: Ord a => AlgebraicGraph a -> Set a
nodes graph = case graph of
              Empty -> Set.empty
              Node x -> Set.fromList [x]
              Overlay g1 g2 -> Set.union (nodes g1) (nodes g2)
              Connect g1 g2 -> Set.union (nodes g1) (nodes g2)


{-
*** TODO ***

Funcția edges din etapa 2.
-}
edges :: Ord a => AlgebraicGraph a -> Set (a, a)
edges graph = case graph of
              Empty -> Set.empty
              Node x -> Set.empty
              Overlay g1 g2 -> Set.union (edges g1) (edges g2)
              Connect g1 g2 -> Set.unions [Set.cartesianProduct (nodes g1) (nodes g2),
                                           edges g1,
                                           edges g2]

{-
*** TODO ***

Funcția outNeighbors din etapa 2.
-}
outNeighbors :: Ord a => a -> AlgebraicGraph a -> Set a
outNeighbors node graph = go graph where
  go graph = case graph of
             Empty -> Set.empty
             Node x -> Set.empty
             Overlay g1 g2 -> Set.union (go g1) (go g2)
             Connect g1 g2 -> if Set.member node (nodes g1) then
                                Set.unions [nodes g2, go g1, go g2]
                             else Set.union (go g1) (go g2)

{-
*** TODO ***

Funcția inNeighbors din etapa 2.
-}
inNeighbors :: Ord a => a -> AlgebraicGraph a -> Set a
inNeighbors node graph = go graph where
  go graph = case graph of
             Empty -> Set.empty
             Node x -> Set.empty
             Overlay g1 g2 -> Set.union (go g1) (go g2)
             Connect g1 g2 -> if Set.member node (nodes g2) then
                                Set.unions [nodes g1, go g1, go g2]
                             else Set.union (go g1) (go g2)

{-
*** TODO 1 (10p) ***

Instanțiați clasa Num cu tipul (AlgebraicGraph a), astfel încât:

* un literal întreg să fie interpretat ca un singur nod cu eticheta egală
    cu acel literal
* operația de adunare să fie intepretată ca Overlay
* operația de înmulțire să fie interpretată drept Connect.

Celelalte funcții din clasă nu sunt relevante. Veți obține warning-uri
pentru neimplementarea lor, dar puteți să le ignorați.

După instanțiere, veți putea evalua în consolă expresii ca:

>>> 1 :: AlgebraicGraph Int
1

>>> 1*(2+3) :: AlgebraicGraph Int
(1*(2+3))
-}
instance Num a => Num (AlgebraicGraph a) where
    fromInteger = Node . fromInteger 
    
    (+) = Overlay
    
    (*) = Connect

{-
*** TODO 2 (10p) ***

Instanțiați clasa Show cu tipul (AlgebraicGraph a), astfel încât reprezentarea 
sub formă de șir de caractere a unui graf să reflecte expresiile aritmetice 
definite mai sus. Puteți pune un nou rând de paranteze pentru fiecare 
subexpresie compusă.

Exemple:

>>> Node 1
1

>>> Connect (Node 1) (Overlay (Node 2) (Node 3))
(1*(2+3))
-}
instance Show a => Show (AlgebraicGraph a) where
    show graph = case graph of
                 Empty -> "_"
                 Node x -> show x
                 Overlay g1 g2 -> "(" ++ show g1 ++ "+" ++ show g2 ++ ")"
                 Connect g1 g2 -> "(" ++ show g1 ++ "*" ++ show g2 ++ ")"


{-
*** TODO 3 (10p) ***

Observați că instanța predefinită de Eq pentru tipul (AlgebraicGraph a)
nu surprinde corect egalitatea a două grafuri, deoarece același graf
conceptual poate avea două descrieri simbolice diferite.

Prin urmare, instanțiați clasa Eq cu tipul (AlgebraicGraph a), astfel încât
să comparați propriu-zis mulțimile de noduri și de arce.

Exemple:

>>> Node 1 == 1
True

>>> Node 1 == 2
False

>>> angle == 1*2 + 1*3
True

>>> triangle == (1*2)*3
True
-}
instance Ord a => Eq (AlgebraicGraph a) where
    g1 == g2 = edges g1 == edges g2

{-
*** TODO 4 (15p) ***

Implementați funcția extend, care extinde un graf existent, atașând noi 
subgrafuri arbitrare în locul nodurilor individuale. Funcția primită ca prim 
parametru determină această corespondență între noduri și subgrafuri. Observați 
că tipul etichetelor noi (b) poate diferi de al etichetelor vechi (a).

Exemplu:

>>> extend (\n -> if n == 1 then 4+5 else Node n) $ 1*(2+3)
((4+5)*(2+3))
-}
extend :: (a -> AlgebraicGraph b) -> AlgebraicGraph a -> AlgebraicGraph b
extend f graph = go graph where
  go graph = case graph of
             Empty -> Empty
             Node x -> f x
             Overlay g1 g2 -> Overlay (go g1) (go g2)
             Connect g1 g2 -> Connect (go g1) (go g2)


{-
*** TODO 5 (15p) ***

Implementați funcția splitNode, care divizează un nod în mai multe noduri,
cu eliminarea nodului inițial. Arcele în care era implicat vechiul nod trebuie 
să devină valabile pentru noile noduri.

CONSTRÂNGERI:

* Implementați splitNode folosind extend!

Exemple:

>>> splitNode 2 (Set.fromList [4,5]) triangle
(1*((4+(5+_))*3))
-}
splitNode :: Ord a
          => a                 -- nodul divizat
          -> Set a             -- nodurile cu care este înlocuit
          -> AlgebraicGraph a  -- graful existent
          -> AlgebraicGraph a  -- graful obținut
splitNode node targets graph = 
    let
        fromAlgebricToGraph = foldr (\t acc -> Overlay (Node t) acc)
    in
        extend (\n -> if n == node then fromAlgebricToGraph Empty targets
                    else Node n) graph
                            

{-
*** TODO 6 (5p) ***

Instanțiați clasa Functor cu constructorul de tip AlgebraicGraph, astfel încât 
să puteți aplica o funcție pe toate etichetele unui graf. fmap reprezintă 
generalizarea lui map pentru alte structuri.

CONSTRÂNGERI:

* Implementați fmap folosind extend!

Exemple:

>>> fmap (+ 10) $ 1*(2+3) :: AlgebraicGraph Int
(11*(12+13))
-}
instance Functor AlgebraicGraph where
    -- fmap :: (a -> b) -> AlgebraicGraph a -> AlgebraicGraph b
    fmap f graph = extend (\n -> Node $ f n) graph

{-
*** TODO 7 (10p) ***

Implementați funcția mergeNodes, care îmbină mai multe noduri într-unul singur, 
pe baza unei proprietăți respectate de nodurile îmbinate, cu eliminarea 
acestora. Arcele în care erau implicate vechile noduri vor referi nodul nou.

CONSTRÂNGERI:

* Implementați mergeNodes folosind fmap!

Exemple:

>>> mergeNodes odd 4 triangle
(4*(2*4))
-}
mergeNodes :: (a -> Bool)       -- proprietatea îndeplinită de nodurile îmbinate
           -> a                 -- noul nod
           -> AlgebraicGraph a  -- graful existent
           -> AlgebraicGraph a  -- graful obținut
mergeNodes prop node graph = fmap (\n -> if prop n then node else n) graph
{-
*** TODO 8 (10p) ***

Implementați funcția filterGraph, care filtrează un graf, păstrând doar 
nodurile care satisfac proprietatea dată.

CONSTRÂNGERI:

* Implementați filterGraph folosind extend!

Exemplu:

>>> filterGraph odd triangle
(1*(_*3))
-}
filterGraph :: (a -> Bool) -> AlgebraicGraph a -> AlgebraicGraph a
filterGraph prop graph = extend (\n -> if prop n then Node n else Empty) graph

{-
*** TODO 9 (5p) ***

Implementați funcția removeNode, care întoarce graful rezultat prin eliminarea 
unui nod și a arcelor în care acesta este implicat. Dacă nodul nu există, 
întoarce același graf.

CONSTRÂNGERI:

* Implementați removeNode folosind filterGraph!

Exemplu:

>>> removeNode 2 triangle
(1*(_*3))
-}
removeNode :: Eq a => a -> AlgebraicGraph a -> AlgebraicGraph a
removeNode node graph = filterGraph (not . (== node)) graph
