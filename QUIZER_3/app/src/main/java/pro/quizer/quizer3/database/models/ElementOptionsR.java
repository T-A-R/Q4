package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("relative_id")})
public class ElementOptionsR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "relative_id")
    private Integer relative_id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "jump")
    private Integer jump;

    @ColumnInfo(name = "search")
    private boolean search;

    @ColumnInfo(name = "pre_condition")
    private String pre_condition;

    @ColumnInfo(name = "post_condition")
    private String post_condition;

    @ColumnInfo(name = "prev_condition")
    private String prev_condition;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "order")
    private Integer order;

    @ColumnInfo(name = "number")
    private Integer number;

    @ColumnInfo(name = "polyanswer")
    private boolean polyanswer;

    @ColumnInfo(name = "record_sound")
    private boolean record_sound;

    @ColumnInfo(name = "take_photo")
    private boolean take_photo;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "flip_cols_and_rows")
    private boolean flip_cols_and_rows;

    @ColumnInfo(name = "small_column")
    private boolean small_column;

    @ColumnInfo(name = "rotation")
    private boolean rotation;

    @ColumnInfo(name = "fixed_order")
    private boolean fixed_order;

    @ColumnInfo(name = "min_answers")
    private Integer min_answers;

    @ColumnInfo(name = "max_answers")
    private Integer max_answers;

    @ColumnInfo(name = "open_type")
    private String open_type;

    @ColumnInfo(name = "placeholder")
    private String placeholder;

    @ColumnInfo(name = "unchecker")
    private boolean unchecker;

    @ColumnInfo(name = "start_value")
    private Integer start_value;

    @ColumnInfo(name = "end_value")
    private Integer end_value;

    @ColumnInfo(name = "type_behavior")
    private String type_behavior;

    @ColumnInfo(name = "show_scale")
    private boolean show_scale;

    @ColumnInfo(name = "show_images")
    private boolean show_images;

    @ColumnInfo(name = "unnecessary_fill_open")
    private boolean unnecessary_fill_open;

    @ColumnInfo(name = "type_end")
    private Integer type_end;

    @ColumnInfo(name = "with_card")
    private boolean with_card;

    @ColumnInfo(name = "show_in_card")
    private boolean show_in_card;

    @ColumnInfo(name = "auto_check")
    private boolean auto_check;

    @ColumnInfo(name = "helper")
    private boolean helper;

    @ColumnInfo(name = "photo_answer")
    private boolean photo_answer;

    @ColumnInfo(name = "photo_answer_required")
    private boolean photo_answer_required;

    @ColumnInfo(name = "min_number")
    private Integer min_number;

    @ColumnInfo(name = "max_number")
    private Integer max_number;

    @ColumnInfo(name = "showRandomQuestion")
    private Boolean showRandomQuestion;

    @ColumnInfo(name = "hide_numbers_answers")
    private Boolean hide_numbers_answers;

    @ColumnInfo(name = "optional_question")
    private Boolean optional_question;

    @ColumnInfo(name = "is_cancel_survey")
    private Boolean is_cancel_survey;

    @ColumnInfo(name = "is_use_absentee")
    private Boolean is_use_absentee;

    public ElementOptionsR() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getJump() {
        return jump;
    }

    public void setJump(Integer jump) {
        this.jump = jump;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public String getPre_condition() {
        return pre_condition;
    }

    public void setPre_condition(String pre_condition) {
        this.pre_condition = pre_condition;
    }

    public String getPost_condition() {
        return post_condition;
    }

    public void setPost_condition(String post_condition) {
        this.post_condition = post_condition;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public boolean isPolyanswer() {
        return polyanswer;
    }

    public void setPolyanswer(boolean polyanswer) {
        this.polyanswer = polyanswer;
    }

    public boolean isRecord_sound() {
        return record_sound;
    }

    public void setRecord_sound(boolean record_sound) {
        this.record_sound = record_sound;
    }

    public boolean isTake_photo() {
        return take_photo;
    }

    public void setTake_photo(boolean take_photo) {
        this.take_photo = take_photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFlip_cols_and_rows() {
        return flip_cols_and_rows;
    }

    public void setFlip_cols_and_rows(boolean flip_cols_and_rows) {
        this.flip_cols_and_rows = flip_cols_and_rows;
    }

    public boolean isRotation() {
        return rotation;
    }

    public void setRotation(boolean rotation) {
        this.rotation = rotation;
    }

    public boolean isFixed_order() {
        return fixed_order;
    }

    public void setFixed_order(boolean fixed_order) {
        this.fixed_order = fixed_order;
    }

    public Integer getMin_answers() {
        return min_answers;
    }

    public void setMin_answers(Integer min_answers) {
        this.min_answers = min_answers;
    }

    public Integer getMax_answers() {
        return max_answers;
    }

    public void setMax_answers(Integer max_answers) {
        this.max_answers = max_answers;
    }

    public String getOpen_type() {
        return open_type;
    }

    public void setOpen_type(String open_type) {
        this.open_type = open_type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isUnchecker() {
        return unchecker;
    }

    public void setUnchecker(boolean unchecker) {
        this.unchecker = unchecker;
    }

    public Integer getStart_value() {
        return start_value;
    }

    public void setStart_value(Integer start_value) {
        this.start_value = start_value;
    }

    public Integer getEnd_value() {
        return end_value;
    }

    public void setEnd_value(Integer end_value) {
        this.end_value = end_value;
    }

    public String getType_behavior() {
        return type_behavior;
    }

    public void setType_behavior(String type_behavior) {
        this.type_behavior = type_behavior;
    }

    public boolean isShow_scale() {
        return show_scale;
    }

    public void setShow_scale(boolean show_scale) {
        this.show_scale = show_scale;
    }

    public boolean isShow_images() {
        return show_images;
    }

    public void setShow_images(boolean show_images) {
        this.show_images = show_images;
    }

    public Integer getRelative_id() {
        return relative_id;
    }

    public void setRelative_id(Integer relative_id) {
        this.relative_id = relative_id;
    }

    public boolean isUnnecessary_fill_open() {
        return unnecessary_fill_open;
    }

    public void setUnnecessary_fill_open(boolean unnecessary_fill_open) {
        this.unnecessary_fill_open = unnecessary_fill_open;
    }

    public Integer getType_end() {
        return type_end;
    }

    public void setType_end(Integer type_end) {
        this.type_end = type_end;
    }

    public boolean isWith_card() {
        return with_card;
    }

    public void setWith_card(boolean with_card) {
        this.with_card = with_card;
    }

    public boolean isShow_in_card() {
        return show_in_card;
    }

    public void setShow_in_card(boolean show_in_card) {
        this.show_in_card = show_in_card;
    }

    public boolean isAutoChecked() {
        return auto_check;
    }

    public boolean isAuto_check() {
        return auto_check;
    }

    public void setAuto_check(boolean auto_check) {
        this.auto_check = auto_check;
    }

    public boolean isSmall_column() {
        return small_column;
    }

    public void setSmall_column(boolean small_column) {
        this.small_column = small_column;
    }

    public String getPrev_condition() {
        return prev_condition;
    }

    public void setPrev_condition(String prev_condition) {
        this.prev_condition = prev_condition;
    }

    public boolean isHelper() {
        return helper;
    }

    public void setHelper(boolean helper) {
        this.helper = helper;
    }

    public boolean isPhoto_answer() {
        return photo_answer;
    }

    public void setPhoto_answer(boolean photo_answer) {
        this.photo_answer = photo_answer;
    }

    public boolean isPhoto_answer_required() {
        return photo_answer_required;
    }

    public void setPhoto_answer_required(boolean photo_answer_required) {
        this.photo_answer_required = photo_answer_required;
    }

    public Integer getMin_number() {
        return min_number;
    }

    public void setMin_number(Integer min_number) {
        this.min_number = min_number;
    }

    public Integer getMax_number() {
        return max_number;
    }

    public void setMax_number(Integer max_number) {
        this.max_number = max_number;
    }

    public Boolean getShowRandomQuestion() {
        return showRandomQuestion;
    }

    public void setShowRandomQuestion(Boolean showRandomQuestion) {
        this.showRandomQuestion = showRandomQuestion;
    }

    public Boolean getHide_numbers_answers() {
        return hide_numbers_answers;
    }

    public void setHide_numbers_answers(Boolean hide_numbers_answers) {
        this.hide_numbers_answers = hide_numbers_answers;
    }

    public Boolean getOptional_question() {
        return optional_question;
    }

    public void setOptional_question(Boolean optional_question) {
        this.optional_question = optional_question;
    }

    public Boolean getIs_cancel_survey() {
        return is_cancel_survey;
    }

    public void setIs_cancel_survey(Boolean is_cancel_survey) {
        this.is_cancel_survey = is_cancel_survey;
    }

    public Boolean getIs_use_absentee() {
        return is_use_absentee;
    }

    public void setIs_use_absentee(Boolean is_use_absentee) {
        this.is_use_absentee = is_use_absentee;
    }
}
