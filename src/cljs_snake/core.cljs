(ns cljs-snake.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as rdom]))





;; -------------------------
;; state
;(defstruct Point :x :y)

(def settings {:chessboardWidth 400
               :chessboardHeight 400
               :squareSize 20
               :hiddenLayers 3
               :hiddenNodes 2})

(def app-state (r/atom {
                        :highScore 0
                        :mutationRate 0.05
                        :fps 100
                        :population []}))  ;; elenco di snakes


(defn chessboard-cells-nr [settings]
  (list
    (int (/ (:chessboardWidth settings) (:squareSize settings)))
    (int (/ (:chessboardHeight settings) (:squareSize settings)))))


(defn random-cell [settings]
  ;; posizione di una cella a caso sulla scacchiera
  (list (rand-int 10) (rand-int 10)))

(defn create-snake [settings]
 {  :score 0
    :lifeLeft 200
    :xVel 0
    :yVel 0
    :dead false
    :head {:x 0 :y 0}
    :food {:x 0 :y 0}})


(defn create-population [n]
 {
   :snakes (map #(create-snake settings) (range n))
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

(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red "] "text."]])



;; -------------------------
;; Views

(defn canvas-component [a b c]
 (let [state (r/atom {})] ;; you can include state
   (r/create-class  {
                     :component-did-mount (fn [this] (draw-canvas-contents
                       ;; per ora non so fare altro ... ci sarà un altro modo senza usare getElementById
                                                      (rdom/dom-node  (. js/document (getElementById "canvas")))));(fn [] (println "I mounted"))
                     :display-name "canvas-component"
                      ;; note the keyword for this method
                     :reagent-render  (fn [a b c]
                                          [:div {:style {:margin-left "260px"}}
                                            [:canvas {:id "canvas" :style {:width "800px" :height "800px"}}]])})))

(defn home-page []
 [:div
    [:h2 "Snake AI in clojurescript"]
    [:div
       [:div {:style {:width "250px" :float "left"}}
        [:input {:type "button" :value "start" :on-click (fn[x] (js/console.log "start clicked ..."))}]]
       [canvas-component "a" "b" "c"]]
    [simple-component]])


;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
