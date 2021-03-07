(ns cljs-snake.snake
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]))

;;
;; serve qualcosa del tipo
;; - posizioni del cibo
;;

(def snake1 {:trails (list [10 10] [9 10] [8 10])
             :dead false
             :lifeTime 0
             :dir [1, 0] ;; direction
             :food [21 10]})


(def snake2 {:trails (list [50 10] [49 10] [48 10])
             :dead false
             :lifeTime 0
             :dir [1, 0] ;; direction
             :food [50 50]})


(defn shead [snake]
  "head of the snake"
  (first (:trails snake)))


(defn drop-tail [snake]
  "rimuove ultimo pezzo coda"
  (let [trails (:trails snake)]
    (assoc snake :trails
           (drop-last  trails))))


(defn collide?
  "true se il punto collide con il serpente"
  [snake point]
  (some #(= point %) (:trails snake)))

(defn non-collide?
  "true se il punto NON collide con il serpente"
  [snake point]
  (not (collide? snake point)))

(defn get-random-point
  "torna una posizione casuale sulla scacchiera (da 1 a nr mattonelle)"
  [n]
  [(inc (rand-int n)) (inc (rand-int n))])


(defn find-first
  "primo elemento di una collection che soddisfa condizione f"
  [f coll]
  (first (filter f coll)))


(defn random-points-lazyseq
  "lazy sequenza di punti sulla scacchiera casuali [1 .. max]"
  ([max] (random-points-lazyseq max (get-random-point max)))
  ([max n] (lazy-seq (cons n (random-points-lazyseq max (get-random-point max))))))


(defn get-random-tile
  "torna una posizione casuale VALIDA sulla scacchiera (da 1 a nr mattonelle)"
  [snake settings]
  (find-first #(non-collide? snake %)
              (random-points-lazyseq (:tilesNr settings))))




(defn new-food 
  "riposiziona il cibo FIXME controllare che posizione sia valida"
  [snake settings]
  (assoc snake :food (get-random-tile snake settings)))


(defn has-eaten? 
  "torna true se ha mangiato il frutto"
  [snake]
  (= (shead snake) (:food snake)))


(defn dead? 
  "FIXME controllare che non vada contro se stesso"
  [snake settings]
  (let [lifeTime (:lifeTime snake)
        h1 (shead snake)
        h1x (first h1)
        h1y (second h1)
        tilesNr (:tilesNr settings)]
    (or (>= lifeTime (:snakeLifeMax settings))
        (= h1x (inc tilesNr))
        (= h1x 0)
        (= h1y 0)
        (= h1y (inc tilesNr)))))


(defn alive? [snake settings]
  (not (dead? snake settings)))

