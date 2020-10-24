(ns cljs-snake.core
    (:require
      [cljs-snake.snake :as s]
      [reagent.core :as r]
      [reagent.dom :as rdom]))



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
(def settings {:tilesNr 25
               :tickIntervalMs 250
               :tileSize 20
               :hiddenLayers 3
               :hiddenNodes 2
               :intervalFn (fn [])})



(def app-state (r/atom { :manualMode true
                         :manualRunning false
                         :highScore 0
                         :mutationRate 0.05
                         :fps 100
                         :snake s/snake1
                         :population []}))  ;; elenco di snakes



(defn create-snake [settings]
  s/snake1)


(defn snake-dead [snake settings]
  "controlla se snake è morto e imposta flag corrispondente se necessario"
  (if (s/dead? snake settings)
   (assoc snake :dead true)
   snake))



(defn snake-move [snake settings]
  "muove il serpente e aggiorna, se necessario, flag di morte e aggiorna posizione cibo"
  (let [trails (:trails snake)
        dir (:dir snake)
        s2 (assoc snake :trails
          ;; appendiamo nuova testa
            (conj trails (vec (map + (first trails) dir))))]
       (snake-dead (if (s/has-eaten? s2)
                    (s/new-food s2 settings)
                    (s/drop-tail s2)) settings)))


(defn simula []
  (loop [s (:snake @app-state) result []]
   (if (s/dead? s settings)
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


(defn do-change-snake-dir [snk d]
  (swap! app-state assoc :snake (assoc snk :dir d)))

(defn key-handle [evt]
  (let [key-pressed (.-keyCode evt)
        snk (:snake @app-state)]
   (case key-pressed
     40 (do-change-snake-dir snk [0 1]) ; DOWN
     39 (do-change-snake-dir snk [1 0])
     38 (do-change-snake-dir snk [ 0 -1]); UP
     37 (do-change-snake-dir snk [-1 0]) ; LEFT
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
  (let [isRunning (:manualRunning @app-state)]
   (if isRunning
    (do
     (swap! app-state assoc :snake (snake-move (:snake @app-state) settings))
     (draw-snake (get-canvas) (:snake @app-state) settings)))))


(defn start-tick []
  (let [isRunning (:manualRunning @app-state)]
     (if isRunning
       (do
        (js/clearInterval (:tickFn @app-state))
        (swap! app-state assoc :manualRunning false))
       (do
        (draw-snake (get-canvas) (:snake @app-state) settings)
        (swap! app-state assoc
          :manualRunning true
          :tickFn (js/setInterval tick (:tickIntervalMs settings)))))))

(defn home-page []
 [:div
    [:h2 "Snake in clojurescript"]
    [:div
       [:div {:style {:width "250px"
                      :float "left"}}
        [:input {:type "checkbox"}]
        [:br]
        [:input {:type "button"
                 :value (if (:manualRunning @app-state) "stop" "start")
                 :on-click start-tick}]]
       [canvas-component settings "a" "b" "c"]]])




;; -------------------------
;; Initialize app

(defn mount-root []
   (rdom/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
