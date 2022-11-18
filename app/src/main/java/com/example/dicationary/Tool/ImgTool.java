package com.example.dicationary.Tool;

import java.util.Random;

public class ImgTool {

    public String[] ImgArray={
            "https://pixabay.com/get/g9c2a844d9b2604cfc82214a8a2696f7740432bd167b297cb72bb17dee87ccba6529b753286f8d216993e036610522389f39a7750032176b90d83c0c7b794efba_640.png",
            "https://pixabay.com/get/g6154b8c501d2fe4c2fc455523d7e1b7c8a8cfa330f63b1b64b36168e9513d94001f86c1c50c389e717ebd843d2a6aa041776454d77b83999bf216a66e26450b9_640.jpg",
            "https://pixabay.com/get/g176995a4ae7ca48b1ce03b09a341b67215c4ae3f037c875d069a7f87f7b22e52b06fc846111883279f9da96ef2b933ddedc241352f4a9e6a23f8acb2ca411848_640.png",
            "https://pixabay.com/get/gade5f13729caf0f0f3bb2294c62f3b21ea74c36f6d54ae529d823017d3c7c41b0d670a91574eb6772dc56774a02b62e7_640.png",
            "https://pixabay.com/get/g10e6e5bf3baa66e4165bacb231ecb73a77e146b5b824de95fca0ce5616d05fc0fc1af091e914c1fe53e7f5730902cd250ab5ede01a63d4d6ffb953af0af24374_640.png",
            "https://pixabay.com/get/gbe10d65d55b7a24cec89f1f26125ac64f91a85a2cbfea591d368a1eaf0c066dccde4793c831f4fdf7e31cef66578576a_640.jpg",
            "https://pixabay.com/get/gb7f2f56098f9d9c0d594511cb7d81a7128d7a995aa0749581b49288c91f90d64f380b2d7b26368afbbc2149b4777e80e0f42d0fcace5800827b0e44f6f2d9869_640.jpg",
            "https://pixabay.com/get/ge4048360ba6293159bd8694747dfd3efca061a860ccc5d764c92a9b7c67abfa705abae9c3b93ead1673586e5e75f7e1bd8acf47d0497552cdb1af81368015e28_640.jpg"
        };

    public String ImgArray() {
        int flag=new Random().nextInt(8);//随机0至7的数
        return ImgArray[flag];
    }
}
