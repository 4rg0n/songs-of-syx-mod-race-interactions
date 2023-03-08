package com.github.argon.sos.interactions.util.json;

import lombok.Getter;
import snake2d.Errors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class JsonParser {
    private final String content;

    @Getter
    private final HashMap<String, Value> map;
    private final LinkedList<String> keys;
    private int i;
    final String path;
    private int line;
    private final int linestart;
    private final int end;

    JsonParser(String content, String errorPath) {
        this(content, errorPath, 0, content.length(), 1);
    }

    private JsonParser(String content, String errorPath, int index, int end, int linestart) {
        this.map = new HashMap();
        this.keys = new LinkedList();
        this.i = 0;
        this.content = content;
        this.path = errorPath;
        this.line = linestart;
        this.linestart = linestart;
        this.i = index;
        this.end = end;

        try {
            while(this.nextChar()) {
                String key = this.getKey();
                Value v = this.getValue();
                if (this.map.containsKey(key)) {
                    this.throwError("Duplicate entry: " + key);
                }

                this.map.put(key, v);
                this.keys.add(key);
            }
        } catch (StringIndexOutOfBoundsException var8) {
            var8.printStackTrace();
            this.throwError("unexpected end of file after line: " + this.line + ", char: " + index);
        }

    }

    private boolean isNewline() {
        char c = this.content.charAt(this.i);
        if (c == '\r' && this.i < this.end - 2 && this.content.charAt(this.i + 1) == '\n') {
            ++this.line;
            this.i += 2;
            return true;
        } else if (c == '\r') {
            ++this.line;
            ++this.i;
            return true;
        } else if (c == '\n') {
            ++this.line;
            ++this.i;
            return true;
        } else {
            return false;
        }
    }

    private boolean nextChar() {
        boolean comment = false;

        while(true) {
            while(this.i < this.end - 1) {
                if (this.content.charAt(this.i) == '*' && this.i < this.end - 1 && this.content.charAt(this.i + 1) == '*') {
                    comment = true;
                    ++this.i;
                } else if (this.isNewline()) {
                    comment = false;
                } else {
                    char c = this.content.charAt(this.i);
                    if (comment) {
                        ++this.i;
                    } else {
                        if (c != ' ' && c != '\t') {
                            return true;
                        }

                        ++this.i;
                    }
                }
            }

            return false;
        }
    }

    private boolean startsWithChar(int start, int end, char open) {
        int oi = this.i;
        int ol = this.line;
        this.i = start;
        if (this.nextChar() && this.content.charAt(this.i) == open) {
            this.i = oi;
            this.line = ol;
            return true;
        } else {
            this.i = oi;
            this.line = ol;
            return false;
        }
    }

    private String getKey() {
        int i2 = this.i;

        for(int l = this.line; this.content.charAt(i2) != ':'; ++i2) {
            if (i2 >= this.end - 1) {
                this.throwError("Expecting a keyword followed by a ':' after line: " + l);
            }
        }

        String key = this.content.substring(this.i, i2).trim();
        this.i = i2 + 1;
        this.nextChar();
        return key;
    }

    private Value getValue() {
        char c = this.content.charAt(this.i);
        int l = this.line;
        if (c == '"') {
            return this.findValue('"', '"');
        } else if (c == '{') {
            return this.findValue('{', '}');
        } else if (c == '[') {
            return this.findValue('[', ']');
        } else {
            int start = this.i;

            do {
                if (++this.i >= this.end) {
                    int i2 = this.i - 10 >= 0 ? this.i - 10 : 0;
                    String after = i2 < this.i - 1 ? this.content.substring(i2, this.i - 1) : " ";
                    this.throwError("Expecting: ',' after: '" + after + "'.");
                    return null;
                }

                if (this.isNewline()) {
                    this.throwError("Expecting: ','");
                }
            } while(this.content.charAt(this.i) != ',');

            ++this.i;
            return new Value(start, this.i - 1, l);
        }
    }

    private Value findValue(char open, char close) {
        int nesting = 0;
        int start = this.i;
        int l = this.line;
        ++this.i;

        while(true) {
            do {
                if (this.i >= this.end) {
                    this.throwError("Expecting a close : " + close + " followed by a ',' after line " + l);
                }
            } while(this.isNewline());

            char c = this.content.charAt(this.i);
            if (c == close) {
                if ((this.i == this.end - 1 || this.content.charAt(this.i + 1) == ',') && nesting == 0) {
                    this.i += 2;
                    return new Value(start, this.i, l);
                }

                --nesting;
            }

            if (c == open) {
                ++nesting;
            }

            ++this.i;
        }
    }

    private void throwError(String error) {
        String m = "Error parsing line: " + this.line + ". " + error;
        throw new Errors.DataError(m, this.path);
    }

    public void throwError(String error, CharSequence key) {
        Value v = (Value)this.map.get(key);
        if (v != null) {
            String m = "Error parsing line: " + v.line + ", key: " + key + ". " + error;
            throw new Errors.DataError(m, this.path);
        } else {
            throw new Errors.DataError(error + ". Error parsing key " + key, this.path);
        }
    }

    public String getError(String error, CharSequence key) {
        Value v = (Value)this.map.get(key);
        return v != null ? error + " Key: " + key + " line: " + v.line + " " + this.path : error + ". Error parsing key " + key + " " + this.path;
    }

    private Value testKey(String key) {
        if (!this.map.containsKey(key)) {
            throw new Errors.DataError("Missing property: " + key + ", in object starting at line: " + this.linestart, this.path);
        } else {
            return (Value)this.map.get(key);
        }
    }

    private Value testKey(CharSequence key, char start, char end, String type) {
        if (!this.map.containsKey(key)) {
            throw new Errors.DataError("Missing property: " + key + ", in object starting at line: " + this.linestart, this.path);
        } else {
            Value v = (Value)this.map.get(key);
            if (this.content.charAt(v.start) != start) {
                throw new Errors.DataError("Expecting a " + type + " (" + start + end + ") at line: " + v.line, this.path);
            } else {
                return v;
            }
        }
    }

    private boolean isKey(CharSequence key, char start, char end) {
        if (!this.map.containsKey(key)) {
            return false;
        } else {
            Value v = (Value)this.map.get(key);
            return this.content.charAt(v.start) == start;
        }
    }

    private Value[] array(int line, int start, int end, String kay) {
        if (this.content.charAt(start) != '[') {
            this.throwError("Expecting an array ([]) Key: " + kay);
        }

        this.line = line;
        this.i = start + 1;
        int size = 0;

        while(true) {
            if (this.i >= end || !this.nextChar()) {
                this.throwError("Expecting ']'");
            }

            if (this.content.charAt(this.i) == ']') {
                Value[] res = new Value[size];
                this.line = line;
                this.i = start + 1;
                size = 0;

                while(true) {
                    if (this.i >= end || !this.nextChar()) {
                        this.throwError("Expecting ']'");
                    }

                    if (this.content.charAt(this.i) == ']') {
                        return res;
                    }

                    res[size++] = this.getValue();
                }
            }

            this.getValue();
            ++size;
        }
    }

    private String string(Value v, CharSequence key) {
        String s = this.content.substring(v.start + 1, v.end - 2);
        s = s.replaceAll("[\\t\\n\\r]+", "");
        s = s.replaceAll("%r%", "\n");
        return s;
    }

    public String string(CharSequence key) {
        Value v = this.testKey(key, '"', '"', "String");
        return this.string(v, key);
    }

    public String[] strings(String key) {
        Value v = this.testKey(key);
        Value[] vv = this.array(v.line, v.start, v.end, key);
        String[] res = new String[vv.length];

        for(int i = 0; i < vv.length; ++i) {
            res[i] = this.string(vv[i], key);
        }

        return res;
    }

    public JsonParser json(String key) {
        Value v = this.testKey(key, '{', '}', "Object");
        return new JsonParser(this.content, this.path, v.start + 1, v.end - 1, v.line);
    }

    public boolean jsonIs(String key) {
        return this.isKey(key, '{', '}');
    }

    public boolean jsonsIs(String key) {
        if (this.arrayIs(key)) {
            Value v = (Value)this.map.get(key);
            return this.startsWithChar(v.start + 1, v.end, '{');
        } else {
            return false;
        }
    }

    public boolean arrayIs(String key) {
        return this.isKey(key, '[', ']');
    }

    public boolean arrayArrayIs(String key) {
        if (this.arrayIs(key)) {
            Value v = (Value)this.map.get(key);
            return this.startsWithChar(v.start + 1, v.end, '[');
        } else {
            return false;
        }
    }

    public JsonParser[] jsons(String key) {
        Value v = this.testKey(key);
        Value[] vv = this.array(v.line, v.start, v.end, key);
        JsonParser[] res = new JsonParser[vv.length];

        for(int i = 0; i < vv.length; ++i) {
            res[i] = new JsonParser(this.content, this.path, vv[i].start + 1, vv[i].end - 1, vv[i].line);
        }

        return res;
    }

    public String value(String key) {
        Value v = this.testKey(key);
        String s = this.content.substring(v.start, v.end);
        return s;
    }

    public String[] values(String key) {
        Value v = this.testKey(key);
        Value[] vv = this.array(v.line, v.start, v.end, key);
        String[] res = new String[vv.length];

        for(int i = 0; i < vv.length; ++i) {
            res[i] = this.content.substring(vv[i].start, vv[i].end);
        }

        return res;
    }

    public boolean test(String key) {
        return this.map.containsKey(key);
    }

    public int count() {
        return this.map.size();
    }

    public List<String> keys() {
        return this.keys;
    }

    public static class Value {
        private final int start;
        private final int end;
        private final int line;

        Value(int start, int end, int line) {
            this.start = start;
            this.end = end;
            this.line = line;
        }
    }
}
