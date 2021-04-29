package org.voicemessanger.client.voice;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;


public class Mixer {

    public static Mixer createDefault() {
        return new Mixer(AudioSystem.getMixerInfo());
    }

    private List<Info<SourceDataLine.Info>> sources = new ArrayList<>();
    private List<Info<TargetDataLine.Info>> targets = new ArrayList<>();

    private Mixer(javax.sound.sampled.Mixer.Info[] infoList) {
        for (javax.sound.sampled.Mixer.Info info : infoList) {
            javax.sound.sampled.Mixer mixer = AudioSystem.getMixer(info);

            Line.Info[] source = mixer.getSourceLineInfo();
            for (Line.Info sourceInfo : source) {
                if (sourceInfo instanceof SourceDataLine.Info) {
                    sources.add(new Info<>(mixer, (SourceDataLine.Info) sourceInfo));
                    break;
                }
            }

            Line.Info[] target = mixer.getTargetLineInfo();
            for (Line.Info targetInfo : target) {
                if (targetInfo instanceof TargetDataLine.Info) {
                    targets.add(new Info<>(mixer, (TargetDataLine.Info) targetInfo));
                    break;
                }
            }

        }
    }

    public String[] getInputNameList() {
        return getStrings(targets);
    }

    public String[] getOutputNameList() {
        return getStrings(sources);
    }

    private String[] getStrings(List<Info<DataLine.Info>> infoList) {
        String[] names = new String[infoList.size()];
        for (int i = 0, infoListSize = infoList.size(); i < infoListSize; i++) {
            Info<DataLine.Info> info = infoList.get(i);
            names[i] = info.mixer.getMixerInfo().getName();
        }
        return names;
    }

    public Line.Info getInputByName(String name) {
        return getByName(targets, name);
    }

    public Line.Info getOutputByName(String name) {
        return getByName(sources, name);
    }

    private <T extends Line.Info> T getByName(List<Info<T>> list, String name) {
        for (Info<T> info : list) {
            if (name.equals(info.mixer.getMixerInfo().getName())) {
                return info.line;
            }
        }
        return null;
    }

    public static class Info<T extends Line.Info> {
        final javax.sound.sampled.Mixer mixer;
        final T line;

        Info(javax.sound.sampled.Mixer mixer, T line) {
            this.mixer = mixer;
            this.line = line;
        }
    }

}
