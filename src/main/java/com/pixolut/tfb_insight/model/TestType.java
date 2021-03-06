package com.pixolut.tfb_insight.model;

import act.Act;
import act.db.morphia.MorphiaQuery;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;

import java.util.List;
import java.util.Map;

/**
 * The test types.
 */
public enum TestType {
    db() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.db;
        }
    },
    fortune() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.fortune;
        }
    },
    json() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.json;
        }

        @Override
        public boolean isDbTest() {
            return false;
        }
    },
    plaintext() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.plaintext;
        }

        @Override
        public boolean isDbTest() {
            return false;
        }

        @Override
        public List<String> roundLabels() {
            return C.list("256", "1024", "4096", "16384");
        }
    },
    query() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.query;
        }

        @Override
        public Test.Result pickOne(List<Test.Result> results) {
            return results.get(results.size() - 1);
        }

        @Override
        public List<String> roundLabels() {
            return C.list("1", "5", "10", "15", "20");
        }
    },
    update() {
        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            return rawData.update;
        }

        @Override
        public Test.Result pickOne(List<Test.Result> results) {
            return results.get(results.size() - 1);
        }

        @Override
        public List<String> roundLabels() {
            return C.list("1", "5", "10", "15", "20");
        }
    },
    density() {
        @Override
        public MorphiaQuery<Project> applyTo(MorphiaQuery<Project> query) {
            return query.orderBy("-density");
        }

        @Override
        public Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData) {
            throw E.unsupport();
        }

        @Override
        public List<String> roundLabels() {
            throw E.unsupport();
        }

    };

    public abstract Map<String, List<Test.Result>> fetch(Test.Result.RawData rawData);

    public MorphiaQuery<Project> applyTo(MorphiaQuery<Project> query) {
        query.orderBy("-tests.bestResult." + name() + ".totalRequests");
        return query;
    }

    public Test.Result pickOne(List<Test.Result> results) {
        return Test.Result.bestOf(results);
    }

    public boolean isDbTest() {
        return true;
    }

    public String label(Project project, Test test) {
        S.Buffer buf = S.buffer("[").append(project.language).append("] ").append(project.framework);
        if (!isDbTest()) {
            return buf.toString();
        }
        return buf.append(" | ").append(test.database).toString();
    }

    public List<String> roundLabels() {
        return C.list("8", "16", "32", "64", "128", "256");
    }

    public float densityWeight() {
        String s = (String) Act.appConfig().get("density." + name());
        return Float.parseFloat(s);
    }
}
