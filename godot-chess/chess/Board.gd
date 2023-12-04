extends TileMap

const BOARD_MAX_SIZE = 8
const PIECE_TYPE_MAP = {0: "B", 1: "K", 2: "N", 3: "P", 4: "Q", 5: "R"}
const PIECE_TYPE_STRING = "BKNPQR"
const PAWN_MOVES = [Vector2i(1, 1), Vector2i(-1, 1), Vector2i(0, 1)]
const KNIGHT_MOVES = [Vector2i(2, 1), Vector2i(1, 2), Vector2i(-2, 1), Vector2i(-1, 2), \
		Vector2i(2, -1), Vector2i(1, -2), Vector2i(-2, -1), Vector2i(-1, -2)]
const ROOK_MOVES = [Vector2i(1, 0), Vector2i(0, 1), Vector2i(-1, 0), Vector2i(0, -1)]
const BISHOP_MOVES = [Vector2i(1, 1), Vector2i(-1, 1), Vector2i(1, -1), Vector2i(-1, -1)]
const KING_QUEEN_MOVES = ROOK_MOVES + BISHOP_MOVES

var piece_scene : PackedScene = preload("res://chess/Piece.tscn")
var pieces = Array()
var last_move = Array()

var white_to_move = true
var selected_square = null
var piece_selected = null
var promote_type = ""

signal generate_move_label(move_text)
signal display_promotion_screen(color, square)


# Called when the node enters the scene tree for the first time.
func _ready():
	for square in get_used_cells(1):
		if square.y == 0:
			create_major_pieces(square, false)
		elif square.y == 1:
			create_piece(square, "P", false)
		elif square.y == 6:
			create_piece(square, "P", true)
		elif square.y == 7:
			create_major_pieces(square, true)

func create_major_pieces(square, color):
	if square.x == 0 or square.x == 7:
		create_piece(square, "R", color)
	elif square.x == 1 or square.x == 6:
		create_piece(square, "N", color)
	elif square.x == 2 or square.x == 5:
		create_piece(square, "B", color)
	elif square.x == 3:
		create_piece(square, "Q", color)
	elif square.x == 4:
		create_piece(square, "K", color)

func create_piece(square, type, color, moves = 0, visibility = true):
	var piece = piece_scene.instantiate()
	piece.square = square
	piece.type = type
	piece.color = color
	piece.moves = moves
	piece.visible = visibility
	if visibility:
		pieces.append(piece)
	add_child(piece)
	return piece


func _input(event):
	var mouse_square = local_to_map(get_global_mouse_position())
	if selected_piece(event, mouse_square):
		piece_selected = get_piece(mouse_square)
		selected_square = mouse_square
		highlight_square(mouse_square)
	elif moved_piece(event):
		if piece_selected.valid_moves.has(mouse_square):
			unhighlight_squares(last_move)
			last_move = [selected_square, mouse_square]
			selected_square = null
			piece_selected = null
			highlight_square(mouse_square)
		else:
			unhighlight_squares([selected_square])
			selected_square = null
			piece_selected = null

func selected_piece(event, mouse_square) -> bool:
	if not event.is_action_pressed("click"):
		return false
	if piece_selected != null:
		return false
	if not piece_exists(mouse_square) or get_piece(mouse_square).color != white_to_move:
		return false
	return true

func moved_piece(event) -> bool:
	if not event.is_action_pressed("click") and not event.is_action_released("click"):
		return false
	if piece_selected == null:
		return false
	return true

func highlight_square(square):
	set_cell(1, square, 1, get_cell_atlas_coords(1, square) + Vector2i(2, 0))

func unhighlight_squares(squares):
	for square in squares:
		set_cell(1, square, 1, get_cell_atlas_coords(1, square) - Vector2i(2, 0))


func find_valid_moves(piece, validate_check) -> Array:
	if piece.type == "B":
		return scan_valid_moves(piece, BISHOP_MOVES, false, validate_check)
	elif piece.type == "K":
		return scan_valid_moves(piece, KING_QUEEN_MOVES, true, validate_check) + \
				scan_valid_castle_moves(piece, validate_check)
	elif piece.type == "N":
		return scan_valid_moves(piece, KNIGHT_MOVES, true, validate_check)
	elif piece.type == "P":
		return scan_valid_pawn_moves(piece, validate_check)
	elif piece.type == "Q":
		return scan_valid_moves(piece, KING_QUEEN_MOVES, false, validate_check)
	elif piece.type == "R":
		return scan_valid_moves(piece, ROOK_MOVES, false, validate_check)
	else:
		return Array()

func scan_valid_moves(piece, moves, once, validate_check) -> Array:
	var result = Array()
	for move in moves:
		var next_square = piece.square
		var repeat = true
		while repeat:
			next_square += move
			if out_of_bounds(next_square):
				break
			if piece_exists(next_square) or once:
				repeat = false
			if valid_move(piece, next_square, false, false, validate_check):
				result.append(next_square)
	return result

func scan_valid_pawn_moves(piece, validate_check) -> Array:
	var result = Array()
	var y_diff = 1 - 2 * int(piece.color) # -1 for white, +1 for black
	var color_vec = Vector2i(1, y_diff)
	for move in PAWN_MOVES:
		var next_square = piece.square + move * color_vec
		# No-capture move
		if move.x == 0:
			if valid_move(piece, next_square, false, true, validate_check):
				result.append(next_square)
			if piece.moves == 0 and not piece_exists(next_square):
				var next_next_square = next_square + move * color_vec
				if valid_move(piece, next_next_square, false, true, validate_check):
					result.append(next_next_square)
		# Capture-only move
		else:
			if valid_move(piece, next_square, true, false, validate_check):
				result.append(next_square)
			elif is_valid_en_passant(piece, next_square, y_diff, validate_check):
				result.append(next_square)
	return result

func is_valid_en_passant(piece, next_square, y_diff, validate_check):
	if not valid_move(piece, next_square, false, true, validate_check):
		return false
	if last_move.size() == 0 or last_move[0] != next_square + Vector2i(0, y_diff) \
			or last_move[1] != next_square - Vector2i(0, y_diff):
		return false
	if not piece_exists(last_move[1]) or get_piece(last_move[1]).type != "P":
		return false
	return true

func scan_valid_castle_moves(piece, validate_check) -> Array:
	var result = Array()
	if not validate_check or piece.moves > 0 or under_attack(piece.square, piece.color):
		return result
	var rook_square = Vector2i(0, piece.square.y)
	var can_queen_castle = not piece_exists(piece.square - Vector2i(3, 0)) \
			and piece_exists(rook_square) and get_piece(rook_square).moves == 0
	rook_square = Vector2i(7, piece.square.y)
	var can_king_castle = piece_exists(rook_square) and get_piece(rook_square).moves == 0
	for i in range(1, 3):
		var next_square = piece.square - Vector2i(i, 0)
		if under_attack(next_square, piece.color) or piece_exists(next_square):
			can_queen_castle = false
		next_square = piece.square  + Vector2i(i, 0)
		if under_attack(next_square, piece.color) or piece_exists(next_square):
			can_king_castle = false
	if can_queen_castle:
		result.append(piece.square - Vector2i(2, 0))
	if can_king_castle:
		result.append(piece.square + Vector2i(2, 0))
	return result

func no_moves() -> bool:
	for piece in pieces:
		if piece.color == white_to_move and not find_valid_moves(piece, true).is_empty():
			return false
	return true

func under_attack(square, color) -> bool:
	for enemy_piece in pieces:
		if enemy_piece.color != color and find_valid_moves(enemy_piece, false).has(square):
			return true
	return false

func under_check() -> bool:
	for piece in pieces:
		if piece.color == white_to_move and piece.type == "K":
			return under_attack(piece.square, piece.color)
	return false

func will_be_under_check(piece, next_square) -> bool:
	var next_square_piece = null
	var prev_square = piece.square
	if piece_exists(next_square):
		var next_piece = get_piece(next_square)
		next_square_piece = create_piece(next_piece.square, next_piece.type, \
				next_piece.color, next_piece.moves, false)
	make_move(piece, next_square, false)
	var is_under_check = under_check()
	undo_move(piece, prev_square, next_square_piece, false)
	return is_under_check

func valid_move(piece, next_square, capture_only, empty_only, validate_check) -> bool:
	if out_of_bounds(next_square):
		return false
	var next_piece = get_piece(next_square)
	if next_piece != null and next_piece.color == piece.color:
		return false
	if capture_only and not piece_exists(next_square):
		return false
	if empty_only and piece_exists(next_square):
		return false
	if validate_check and will_be_under_check(piece, next_square):
		return false
	return true

func make_move(piece, next_square, flip_turn):
	var previous_square = piece.square
	var previous_type = piece.type
	var promote_type = ""
	var is_queen_castle = piece.type == "K" and piece.square - next_square == Vector2i(2, 0)
	var is_king_castle = piece.type == "K" and next_square - piece.square == Vector2i(2, 0)
	if flip_turn:
		# Handle promotion first
		if piece.type == "P" and next_square.y == 7 - 7 * int(piece.color):
			promote_type = await handle_promotion(next_square, piece.color)
			# Reset if promotion cancelled
			if promote_type == "":
				piece.position = map_to_local(piece.square)
				return
		# Handle castling
		if is_queen_castle:
			make_move(get_piece(Vector2i(0, piece.square.y)), piece.square - Vector2i(1, 0), false)
		elif is_king_castle:
			make_move(get_piece(Vector2i(7, piece.square.y)), piece.square + Vector2i(1, 0), false)
		white_to_move = not white_to_move
	# Update pieces
	var is_capture = false
	if piece_exists(next_square):
		is_capture = true
		var next_piece = get_piece(next_square)
		pieces.erase(next_piece)
		next_piece.queue_free()
	# Handle en passant
	if flip_turn and not is_capture and piece.type == "P" and piece.square.x != next_square.x:
		is_capture = true
		var capture_square = next_square - Vector2i(0, 1 - 2 * int(piece.color))
		var next_piece = get_piece(capture_square)
		pieces.erase(next_piece)
		next_piece.queue_free()
	piece.position = map_to_local(next_square)
	piece.square = next_square
	piece.moves += 1
	if promote_type.length() > 0:
		piece.type = promote_type
	# Generate move label last
	if flip_turn:
		generate_move_label.emit(get_move_text(piece, previous_square, previous_type, \
				is_capture, is_queen_castle, is_king_castle))
	get_parent().promote_type = "" # Reset game promote type

func undo_move(piece, prev_square, next_square_piece, flip_turn):
	piece.position = map_to_local(prev_square)
	piece.square = prev_square
	piece.moves -= 1
	if next_square_piece != null:
		next_square_piece.visible = true
		pieces.append(next_square_piece)
	if flip_turn:
		white_to_move = not white_to_move

func get_move_text(piece, previous_square, previous_type, is_capture, \
					is_queen_castle, is_king_castle) -> String:
	var move_text = ""
	if is_king_castle:
		move_text = "O-O"
	elif is_queen_castle:
		move_text = "O-O-O"
	else:
		var is_promote = piece.type != previous_type
		if previous_type != "P":
			move_text += previous_type
		if is_capture:
			if previous_type == "P":
				move_text += String.chr(previous_square.x + 97)
			move_text += "x"
		move_text += String.chr(piece.square.x + 97) + str(8 - piece.square.y)
		if is_promote:
			move_text += "=" + piece.type
	var is_check = under_check()
	var is_checkmate = no_moves() and is_check
	if is_checkmate:
		move_text += "#"
	elif is_check:
		move_text += "+"
	return move_text

func out_of_bounds(square) -> bool:
	return not get_used_cells(1).has(square)

func piece_exists(square) -> bool:
	return get_piece(square) != null

func get_piece(square) -> Sprite2D:
	for piece in pieces:
		if piece.square == square:
			return piece
	return null

func handle_promotion(square, color) -> String:
	display_promotion_screen.emit(square, color)
	await get_parent().continue_board
	return get_parent().promote_type
