module AlgebraicGraph where

import Data.Set (Set)
import qualified Data.Set as Set

data AlgebraicGraph a
    = Empty
    | Node { label :: a }
    | Overlay { left :: AlgebraicGraph a, right :: AlgebraicGraph a }
    | Connect { left :: AlgebraicGraph a, right :: AlgebraicGraph a }
    deriving (Eq, Ord, Show)

-- (1, 2), (1, 3)
angle :: AlgebraicGraph Int
angle = Connect (Node 1) (Overlay (Node 2) (Node 3))

-- (1, 2), (1, 3), (2, 3)
triangle :: AlgebraicGraph Int
triangle = Connect (Node 1) (Connect (Node 2) (Node 3))

{-
*** TODO 1 (10p) ***

Implementați funcția nodes, care întoarce mulțimea nodurilor grafului.

Hint: Set.union

Exemple:

>>> nodes triangle
fromList [1,2,3]
-}
nodes :: Ord a => AlgebraicGraph a -> Set a
nodes graph = case graph of
              Empty -> Set.empty
              Node x -> Set.fromList [x]
              Overlay g1 g2 -> Set.union (nodes g1) (nodes g2)
              Connect g1 g2 -> Set.union (nodes g1) (nodes g2)

{-
*** TODO 2 (10p) ***

Implementați funcția edges, care întoarce mulțimea arcelor grafului.

Hint: Set.union, Set.cartesianProduct

Exemple:

>>> edges triangle
fromList [(1,2),(1,3),(2,3)]
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
*** TODO 3 (15p) ***

Implementați funcția outNeighbors, care întoarce mulțimea nodurilor înspre care 
pleacă arce dinspre un nod sursă.

CONSTRÂNGERI:

* NU folosiți funcția edges definită mai sus, pentru că ar genera prea multe 
  muchii inutile.
* Evitați trimiterea către apelurile recursive a parametrilor care nu se 
  modifică. De exemplu, parametrul node nu se modifică, în timp ce parametrul 
  graph se modifică. Pentru aceasta, definiți o funcție recursivă locală (de 
  exemplu, în where), care să primească drept parametri doar entitățile 
  variabile de la un apel recursiv la altul.

Exemple:

>>> outNeighbors 1 triangle
fromList [2,3]
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
*** TODO 4 (15p) ***

Implementați funcția inNeighbors, care întoarce mulțimea nodurilor dinspre care 
pleacă arce înspre un nod destinație.

CONSTRÂNGERI:

* NU folosiți funcția edges definită mai sus, pentru că ar genera prea multe 
  muchii inutile.
* Evitați trimiterea către apelurile recursive a parametrilor care nu se 
  modifică (vezi outNeighbors).

Exemple:

>>> inNeighbors 3 triangle
fromList [1,2]
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
*** TODO 5 (15p) ***

Implementați funcția removeNode, care întoarce graful rezultat prin eliminarea 
unui nod și a arcelor în care acesta este implicat. Dacă nodul nu există, 
întoarce același graf.

CONSTRÂNGERI:

* Evitați trimiterea către apelurile recursive a parametrilor care nu se 
  modifică (vezi outNeighbors).

Exemple:

>>> removeNode 2 triangle
Connect {left = Node {label = 1}, right = Connect {left = Empty, right = Node {label = 3}}}

>>> nodes $ removeNode 2 triangle
fromList [1,3]

>>> edges $ removeNode 2 triangle
fromList [(1,3)]
-}
removeNode :: Eq a => a -> AlgebraicGraph a -> AlgebraicGraph a
removeNode node graph = go graph where
              Empty -> Empty
              Node x -> if x == node then Empty else Node x
              Overlay g1 g2-> Overlay go g1 go g2
              Connect g1 g2-> Overlay go g1 go g2

{-
*** TODO 6 (20p) ***

Implementați funcția splitNode, care divizează un nod în mai multe noduri,
cu eliminarea nodului inițial. Arcele în care era implicat vechiul nod trebuie 
să devină valabile pentru noile noduri.

CONSTRÂNGERI:

* Evitați trimiterea către apelurile recursive a parametrilor care nu se 
  modifică (vezi outNeighbors).

Exemple:

>>> splitNode 2 (Set.fromList [4,5]) triangle
Connect {left = Node {label = 1}, right = Connect {left = Overlay {left = Node {label = 4}, right = Overlay {left = Node {label = 5}, right = Empty}}, right = Node {label = 3}}}

>>> nodes $ splitNode 2 (Set.fromList [4,5]) triangle
fromList [1,3,4,5]

>>> edges $ splitNode 2 (Set.fromList [4,5]) triangle
fromList [(1,3),(1,4),(1,5),(4,3),(5,3)]
-}
splitNode :: Ord a
          => a                 -- nodul divizat
          -> Set a             -- nodurile cu care este înlocuit
          -> AlgebraicGraph a  -- graful existent
          -> AlgebraicGraph a  -- graful obținut
splitNode node targets graph = go graph where
  go graph = case graph of
             Empty -> Empty
             Node x -> if x == node then foldr (\node acc -> Overlay (Node node) acc) Empty targets
                       else Node x
             Overlay g1 g2 -> Overlay (go g1) (go g2)
             Connect g1 g2 -> Connect (go g1) (go g2)

{-
*** TODO 7 (15p) ***

Implementați funcția mergeNodes, care îmbină mai multe noduri într-unul singur, 
pe baza unei proprietăți respectate de nodurile îmbinate, cu eliminarea 
acestora. Arcele în care erau implicate vechile noduri vor referi nodul nou.

CONSTRÂNGERI:

* Evitați trimiterea către apelurile recursive a parametrilor care nu se 
  modifică (vezi outNeighbors).

Exemple:

>>> mergeNodes odd 4 triangle
Connect {left = Node {label = 4}, right = Connect {left = Node {label = 2}, right = Node {label = 4}}}

>>> nodes $ mergeNodes odd 4 triangle
fromList [2,4]

>>> edges $ mergeNodes odd 4 triangle
fromList [(2,4),(4,2),(4,4)]
-}
mergeNodes :: (a -> Bool)       -- proprietatea îndeplinită de nodurile îmbinate
           -> a                 -- noul nod
           -> AlgebraicGraph a  -- graful existent
           -> AlgebraicGraph a  -- graful obținut
mergeNodes prop node graph = go graph where
  go graph = case graph of
             Empty -> Empty
             Node x -> if prop x then Node node else Node x
             Overlay g1 g2 -> Overlay (go g1) (go g2)
             Connect g1 g2 -> Connect (go g1) (go g2)
