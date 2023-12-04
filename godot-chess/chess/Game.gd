extends Node2D

var board_scene : PackedScene = preload("res://chess/Board.tscn")
var board
var menu_scene : PackedScene = preload("res://chess/Moves.tscn")
var menu
var promote_scene : PackedScene = preload("res://chess/Promote.tscn")
var promote
var promote_type = ""

signal continue_board

# Called when the node enters the scene tree for the first time.
func _ready():
	board = board_scene.instantiate()
	menu = menu_scene.instantiate()
	menu.position.x = 1200
	board.connect("generate_move_label", menu._on_generate_move_label)
	board.connect("display_promotion_screen", _on_display_promotion_screen)
	add_child(board)
	add_child(menu)


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(_delta):
	pass

func _on_display_promotion_screen(square, color):
	promote = promote_scene.instantiate()
	promote.position = board.map_to_local(square) - Vector2(75, 75)
	promote.color = color
	promote.connect("selected_promotion", _on_selected_promotion)
	# promote.connect("selected_promotion", board._on_selected_promotion)
	add_child(promote)

func _on_selected_promotion(type):
	promote_type = type
	promote.queue_free()
	continue_board.emit()
