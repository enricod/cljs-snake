(ns cljs-snake.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as rdom]))


(def snake1 {:trails (list [10 10] [9 10] [8 10])
             :dead false
             :dir [1, 0] ;; direction
             :food [21 10]})


(def snake2 {:trails (list [50 10] [49 10] [48 10])
             :dead false
             :dir [1, 0] ;; direction
             :food [50 50]})


(comment
  (def snake1 {:trails (list [10 10] [9 10] [8 10])
               :dead false
               :dir [1, 0] ;; direction
               :food [50 50]})
  (defn game []
    (while (not (:dead (:snake @app-state)))
     (do
          (println (:snake @app-state))
          (swap! app-state assoc :snake (snake-move (:snake @app-state) settings))))))


(comment
  (loop [x 10 result [] ] (if (= x 0)
                           result
                           (recur (dec x) (conj result x)))))
;;


;; -------------------------
;; state
;;
(def settings {:tilesNr 40
               :tileSize 12
               :hiddenLayers 3
               :hiddenNodes 2})


(def app-state (r/atom { :manualMode true
                         :running true
                         :highScore 0
                         :mutationRate 0.05
                         :fps 100
                         :snake snake1
                         :population []}))  ;; elenco di snakes





(defn get-random-tile [snake settings]
  "FIXME da verificare, perchè non deve tornare alcuno 0"
  (let [n (:tilesNr settings)]
    [(rand-int n) (rand-int n)]))


(defn snake-head [snake]
   "head of the snake"
   (first (:trails snake)))


(defn create-snake [settings]
  snake1)


(defn snake-dead? [snake settings]
  "FIXME controllare che non vada contro se stesso"
  (let [h1 (snake-head snake)
        h1x (first h1)
        h1y (second h1)
        tilesNr (:tilesNr settings)]
   (or    (= h1x (inc tilesNr))
          (= h1x 0)
          (= h1y 0)
          (= h1y (inc tilesNr)))))


(defn snake-alive? [snake settins] (not (snake-dead? snake settings)))


(defn snake-has-eaten? [snake]
  "torna true se ha mangiato il frutto"
  (= (snake-head snake) (:food snake)))


(defn snake-dead [snake settings]
  "controlla se snake è morto e imposta flag corrispondente se necessario"
  (if (snake-dead? snake settings)
   (assoc snake :dead true)
   snake))

(defn snake-drop-tail [snake]
  "rimuove ultimo pezzo coda"
  (let [trails (:trails snake)]
   (assoc snake :trails
     (drop-last  trails))))

(defn snake-new-food [snake settings]
  "riposiziona il cibo FIXME controllare che posizione sia valida"
  (assoc snake :food (get-random-tile snake settings)))

(defn snake-move [snake settings]
  "muove il serpente e aggiorna, se necessario, flag di morte e aggiorna posizione cibo"
  (let [trails (:trails snake)
        dir (:dir snake)
        s2 (assoc snake :trails
          ;; appendiamo nuova testa
            (conj trails (vec (map + (first trails) dir))))]
       (snake-dead (if (snake-has-eaten? s2)
                    (snake-new-food s2 settings)
                    (snake-drop-tail s2)) settings)))


(defn simula []
  (loop [s (:snake @app-state) result []]
   (if (snake-dead? s settings)
    (conj result s)
    (recur (snake-move s settings) (conj result s)))))

;; (repeatedly 10 #(snake-move (:snake @app-state) settings))


(defn create-population [n]
 { :snakes (map #(create-snake settings) (range n))
   :bestSnake 0
   :gen 0
   :bestSnakeScore 0
   :sameBest 0})



(defn draw-canvas-contents [ canvas]
  (let [ ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
    (.beginPath ctx)
    (.moveTo ctx 0 0)
    (.lineTo ctx w h)
    (.moveTo ctx w 0)
    (.lineTo ctx 0 h)
    (.stroke ctx)))


(defn pos-on-canvas [tile]
  (list (* (:tileSize settings) (dec (first tile))) (* (:tileSize settings) (dec (second tile)))))


(defn get-canvas []
  (let [canvas (rdom/dom-node  (. js/document (getElementById "canvas")))]

   canvas))

(defn- disegna-mattonella [ctx pos color wRatio hRatio]
  (do
    (set! (.-fillStyle ctx) color)
    (.fillRect ctx
     (* (first pos) wRatio)
     (* (second pos) hRatio)
     (* (:tileSize settings) wRatio)
     (* (:tileSize settings) hRatio))))


(defn draw-snake [canvas snake settings]
  (let [ ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
       (let [foodPos (pos-on-canvas (:food snake))
             wRatio (/  (.-width canvas) w)
             hRatio (/ (.-height canvas) h)]
         (do
            (println (str "clientw=" w ", clienth=" h ", w=" (.-width canvas) ", h=" (.-height canvas)))
            ;; Clean canvas
            (set! (.-fillStyle ctx) "#ffFFff")
            (.fillRect ctx 0 0 w h)
            ;; CIBO
            (disegna-mattonella ctx foodPos  "#00FF00" wRatio hRatio)
            ;; SERPENTE
            (doall (map #(disegna-mattonella ctx (pos-on-canvas %) "#000000" wRatio hRatio) (:trails snake)))))))






(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]])


(defn key-handle [evt]
  (let [key-pressed (.-keyCode evt)]
   (case key-pressed
     40 (println (str "DOWN" key-pressed))
     39 (println (str "RIGHT " key-pressed))
     38 (println (str "UP " key-pressed))
     37 (println (str "LEFT " key-pressed))
     (println (str "altro " key-pressed)))))



(defn register-key-events []
  (rdom/dom-node  (. js/document (addEventListener "keydown" key-handle))))


;; -------------------------
;; Views
;;
(defn canvas-component [settings a b c]
 (let [state (r/atom {})] ;; you can include state
   (r/create-class  {
                     :component-did-mount (fn [this]
                                           (do
                                             (register-key-events)
                                             (comment (draw-canvas-contents (get-canvas)))))
                     :display-name "canvas-component"
                      ;; note the keyword for this method
                     :reagent-render  (fn [a b c]
                                          [:div {:style {:margin-left "260px"}}
                                            [:canvas {:id "canvas"
                                                      :style {:width (str (*  (:tilesNr settings) (:tileSize settings)) "px")
                                                              :height (str (*  (:tilesNr settings) (:tileSize settings)) "px")}}]])})))




(defn tick []
  (do
   (println "tick")

   (swap! app-state assoc :snake (snake-move (:snake @app-state) settings))
   (draw-snake (get-canvas) (:snake @app-state) settings)))

(defn start-tick []
  (do
    (draw-snake (get-canvas) (:snake @app-state) settings)
    (js/setInterval tick 1000)))

(defn home-page []
 [:div
    [:h2 "Snake in clojurescript"]
    [:div
       [:div {:style {:width "250px"
                      :float "left"}}
        [:input {:type "checkbox"}]
        [:br]
        [:input {:type "button"
                 :value "start"
                 :on-click start-tick}]]
       [canvas-component settings "a" "b" "c"]]
    [simple-component]])



;; -------------------------
;; Initialize app

(defn mount-root []
   (rdom/render [home-page] (.getElementById js/document "app")))




(defn init! []

  (mount-root))
